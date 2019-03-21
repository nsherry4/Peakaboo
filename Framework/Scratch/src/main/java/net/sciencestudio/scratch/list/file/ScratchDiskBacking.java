package net.sciencestudio.scratch.list.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


class ScratchDiskBacking {

	//stores the locations of all entries as offset/length pairs
	private List<LongRange> elementPositions;
	private LongRangeSet discardedRanges;
	
	private File file;
	private RandomAccessFile raf;
		
	
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
		raf = new RandomAccessFile(file, "rw");
	}

	
	private void addEntry(int index, byte[] data)
	{
		

		try {
			
			long currentLength = raf.length();
			long writePosition = currentLength;
					
			List<LongRange> bigRanges = discardedRanges.getRanges().stream().filter(r -> r.size() >= data.length).collect(Collectors.toList());			
			
						
			bigRanges.sort((o1, o2) -> {
				Long s1 = o1.size();
				Long s2 = o2.size();
				return s2.compareTo(s1);
			});
			
			
			if (bigRanges.size() != 0)
			{
				writePosition = bigRanges.get(0).getStart();
			}
			
			raf.seek(writePosition);
			raf.write(data);
			
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
	
		
	public synchronized void add(int index, byte[] element)
	{		
		addEntry(index, element);
	}


	public void clear()
	{
		elementPositions.clear();
		discardedRanges.clear();
		try {
			raf.seek(0);
		} catch (IOException e)
		{
			throw new UnsupportedOperationException("Cannot write to backend file");
		}
	}

	public synchronized byte[] get(int index)
	{
		if (index >= elementPositions.size()) return null;
		LongRange position = elementPositions.get(index);
		if (position == null) return null;
		
		long offset = position.getStart();
		//The positions may be >MAXINT, but the length really shouldn't be
		int length = (int)(position.getStop() - position.getStart() + 1);
		
		byte[] data = new byte[length];
		try
		{
			raf.seek(offset);
			raf.read(data, 0, length);
						
			return data;
		}
		catch (IOException e)
		{
			return null;
		}
		
	}
	

	public void remove(int index)
	{
		discardedRanges.addRange(  elementPositions.remove(index)  );
	}


	public synchronized void set(int index, byte[] data)
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
	protected void finalize()
	{
		try
		{
			raf.close();			
		}
		catch (IOException e)
		{
			
		}
	}
	
	
}
