package org.peakaboo.framework.scratch.list.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


class ScratchDiskBacking implements AutoCloseable {

	//stores the locations of all entries as offset/length pairs
	private List<LongRange> elementPositions;
	private LongRangeSet discardedRanges;
	
	private File file;
	private RandomAccessFile writeRaf; // Single RAF for writes
	private final ConcurrentLinkedQueue<RandomAccessFile> readRafPool = new ConcurrentLinkedQueue<>();
	private volatile boolean closing = false;
		
	
	/**
	 * Create a new AbstractScratchList
	 * @throws IOException
	 */
	public ScratchDiskBacking() throws IOException
	{
		file = File.createTempFile("ScratchDiskList [temp - ", "]");
		file.deleteOnExit();
		elementPositions = new ArrayList<LongRange>();
		discardedRanges = new LongRangeSet();
		writeRaf = new RandomAccessFile(file, "rw");
		
		// Pre-populate pool with a few read handles
		for (int i = 0; i < 4; i++) {
			readRafPool.offer(new RandomAccessFile(file, "r"));
		}
	}

	
	private void addEntry(int index, byte[] data)
	{
		

		try {
			
			long writePosition = writeRaf.length();
					
			// Try to find a suitable discarded range
			LongRange suitableRange = findDiscardedRange(data.length);
			if (suitableRange != null) {
				writePosition = suitableRange.getStart();
			}
			
			writeRaf.seek(writePosition);
			writeRaf.write(data);
			
			if (index >= elementPositions.size())
			{
				for (int i = elementPositions.size(); i <= index; i++)
				{
					elementPositions.add(null);
				}
			}
			LongRange elementPosition = new LongRange((long)writePosition, (long)writePosition+data.length-1);
			elementPositions.set(index, elementPosition);
			discardedRanges.removeRange(elementPosition);
			
		} catch (IOException e)
		{
			throw new UnsupportedOperationException("Cannot write to backend file");
		}
		
		
		
	}
	
		
	public void add(int index, byte[] element)
	{		
		addEntry(index, element);
	}


	public void clear()
	{
		elementPositions.clear();
		discardedRanges.clear();
		try {
			writeRaf.seek(0);
		} catch (IOException e)
		{
			throw new UnsupportedOperationException("Cannot write to backend file");
		}
	}

	/**
	 * Get a read RAF handle from pool or create new one. Returns null if closing.
	 */
	private RandomAccessFile getReadRaf() {
		if (closing) return null;
		
		RandomAccessFile readRaf = readRafPool.poll();
		if (readRaf == null) {
			if (closing) return null; // Check again after poll
			try {
				readRaf = new RandomAccessFile(file, "r");
			} catch (IOException e) {
				return null;
			}
		}
		return readRaf;
	}
	
	/**
	 * Find a suitable discarded range for the given size using bounded best-fit.
	 * @param minSize minimum size needed
	 * @return LongRange that can fit the data, or null if none found
	 */
	private LongRange findDiscardedRange(int minSize) {
		List<LongRange> allRanges = discardedRanges.getRanges();
		// Until we have a good number of spaces to fill, don't bother spending the time on it
		if (allRanges.size() < 10) { return null; }

		LongRange bestFit = null;
		int candidatesFound = 0;
		int maxCandidates = 10; // Limit search to keep O(1) time complexity
		
		for (LongRange range : allRanges) {
			if (range.size() >= minSize) {
				if (bestFit == null || range.size() < bestFit.size()) {
					bestFit = range;
				}
				candidatesFound++;
				if (candidatesFound >= maxCandidates) {
					break; // Stop after finding enough candidates
				}
			}
		}
		
		return bestFit;
	}
	
	/**
	 * Return a read RAF handle to pool or close it if shutting down.
	 */
	private void returnReadRaf(RandomAccessFile readRaf) {
		if (closing) {
			try { 
				readRaf.close(); 
			} catch (IOException ignored) {}
		} else {
			readRafPool.offer(readRaf);
		}
	}
	
	public byte[] get(int index)
	{
		if (index >= elementPositions.size()) return null;
		LongRange position = elementPositions.get(index);
		if (position == null) return null;
		
		long offset = position.getStart();
		//The positions may be >MAXINT, but the length really shouldn't be
		int length = (int)(position.getStop() - position.getStart() + 1);
		
		RandomAccessFile readRaf = getReadRaf();
		if (readRaf == null) return null; // Shutting down
		
		byte[] data = new byte[length];
		try {
			readRaf.seek(offset);
			readRaf.read(data, 0, length);
			return data;
		} catch (IOException e) {
			return null;
		} finally {
			returnReadRaf(readRaf);
		}
	}
	

	public void remove(int index)
	{
		if (index >= elementPositions.size()) return;
		
		LongRange removedRange = elementPositions.remove(index);
		if (removedRange != null) {
			discardedRanges.addRange(removedRange);
		}
	}


	public void set(int index, byte[] data)
	{
		
		if (elementPositions.size() > index)
		{
			//record the old range which is not being used anymore
			LongRange oldRange = elementPositions.get(index);
			discardedRanges.addRange(oldRange);
		}
		
		addEntry(index, data);
	}

	public int size()
	{
		return elementPositions.size();
	}


	
	@Override
	public void close() throws IOException {
		closing = true;
		
		// Close write handle
		if (writeRaf != null) {
			writeRaf.close();
			writeRaf = null;
		}
		
		// Close all read handles currently in pool
		RandomAccessFile readRaf;
		while ((readRaf = readRafPool.poll()) != null) {
			try {
				readRaf.close();
			} catch (IOException ignored) {
				// Ignore cleanup errors
			}
		}
		
		if (file != null && file.exists()) {
			file.delete();
		}
	}
	
	
}
