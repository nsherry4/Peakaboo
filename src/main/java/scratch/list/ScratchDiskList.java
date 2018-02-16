package scratch.list;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import scitypes.LongRange;
import scitypes.LongRangeSet;
import scratch.ScratchEncoder;
import scratch.ScratchException;
import scratch.encoders.serializers.SerializingEncoder;



class ScratchDiskList<T> implements ScratchList<T> {

	//stores the locations of all entries as offset/length pairs
	private List<LongRange> elementPositions;
	private LongRangeSet discardedRanges;
	

	private File file;
	private RandomAccessFile raf;
	
	private ScratchEncoder<T> encoder;
	
	
	
	public ScratchDiskList() throws IOException
	{
		this(new SerializingEncoder());
	}
	
	/**
	 * Create a new AbstractScratchList
	 * @param name the name prefix to give the temporary file
	 * @throws IOException
	 */
	public ScratchDiskList(ScratchEncoder<T> encoder) throws IOException
	{
		this.encoder = encoder;
		file = File.createTempFile("Scratch List [temp - ", "]");
		file.deleteOnExit();
		init();	
	}
	

	private void init() throws FileNotFoundException
	{
		elementPositions = new ArrayList<LongRange>();
		discardedRanges = new LongRangeSet();
		raf = new RandomAccessFile(file, "rw");
	}
	

	
	protected ScratchDiskList(File temp, RandomAccessFile raf, List<LongRange> positions, LongRangeSet discarded)
	{
		elementPositions = new ArrayList<>(positions);
		discardedRanges = discarded;
		this.file = temp;
		this.raf = raf;
	}
	
	
	
	public ScratchEncoder<T> getEncoder() {
		return encoder;
	}


	public void setEncoder(ScratchEncoder<T> encoder) {
		this.encoder = encoder;
	}


	protected final void makeSublist(ScratchDiskList<T> target, int startIndex, int endIndex)
	{
		target.elementPositions = elementPositions.subList(startIndex, endIndex);
		target.discardedRanges = discardedRanges;
		target.file = file;
		target.raf = raf;
	}
	
	
	protected byte[] encodeObject(T element) throws IOException {
		return this.encoder.encode(element);
	}
	
	
	protected T decodeObject(byte[] byteArray) throws IOException {
		return this.encoder.decode(byteArray);
	}


	private void addEntry(int index, T element)
	{
		

		try {
			
			long currentLength = raf.length();
			long writePosition = currentLength;
			
			byte[] encoded = encodeObject(element);
			final int encodedLength = encoded.length;
			
			List<LongRange> bigRanges = discardedRanges.getRanges().stream().filter(r -> r.size() >= encodedLength).collect(Collectors.toList());			
			
						
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
			raf.write(encoded);
			
			if (index >= elementPositions.size())
			{
				for (int i = elementPositions.size(); i <= index; i++)
				{
					elementPositions.add(null);
				}
			}
			LongRange elementPosition = new LongRange((long)writePosition, (long)writePosition+encoded.length-1);
			elementPositions.set(index, elementPosition);
			discardedRanges.removeRange(elementPosition);
			
		} catch (IOException e)
		{
			throw new UnsupportedOperationException("Cannot write to backend file");
		}
		
		
		
	}
	
	
	
	public synchronized void add(T e)
	{
		addEntry(elementPositions.size(), e);
		
	}
	
	public synchronized void add(int index, T element)
	{		
		addEntry(index, element);
	}

	public void addAll(Collection<? extends T> c)
	{
		for (T t : c)
		{
			add(t);
		}
	}

	public void addAll(int index, Collection<? extends T> c)
	{
		for (T t : c)
		{
			add(index, t);
			index++;
		}
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

	public boolean contains(Object o)
	{
		for (T t : this)
		{
			if (t.equals(o)) return true;
		}
		return false;
	}


	@Override
	public synchronized T get(int index)
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
						
			return decodeObject(data);
		}
		catch (IOException e)
		{
			return null;
		}
		
	}
	

	public Iterator<T> iterator()
	{
		return new Iterator<T>() {

			int index = 0;
			
			public boolean hasNext()
			{
				return index < elementPositions.size();
			}

			public T next()
			{
				return ScratchDiskList.this.get(index++);
			}

			public void remove()
			{
				ScratchDiskList.this.remove(index);
			}};
	}

	
	@Override
	public boolean remove(T o)
	{
		T t;
		for (int i = 0; i < elementPositions.size(); i++)
		{
			t = get(i);
			if (t.equals(o))
			{
				remove(i);
				return true;
			}
		}
		
		return false;
		
	}

	@Override
	public void remove(int index)
	{
		discardedRanges.addRange(  elementPositions.remove(index)  );
	}


	@Override
	public synchronized void set(int index, T element)
	{
		
		if (elementPositions.size() > index)
		{
			//record the old range which is not being used anymore
			LongRange oldRange = elementPositions.get(index);
			discardedRanges.addRange(oldRange);
		}
		
		addEntry(index, element);		
	}

	@Override
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
	
	
	public long filesize()
	{
		return elementPositions.stream().map(range -> range.size()).reduce(0l, (a, b) -> a + b);
	}
	
}
