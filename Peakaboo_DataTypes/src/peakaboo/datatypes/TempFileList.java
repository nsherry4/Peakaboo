package peakaboo.datatypes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.sun.media.sound.DataPusher;

import fava.FunctionMap;
import fava.Pair;


public class TempFileList<T> implements List<T>
{

	//stores the locations of all entries as offset/length pairs
	private List<Pair<Long, Integer>> elementPositions;
	
	private File temp;
	private RandomAccessFile raf;
	
	private FunctionMap<T, byte[]> encode;
	private FunctionMap<byte[], T> decode;
	
	private T lastElement = null;
	private int lastElementIndex = -1;
	
	public TempFileList(int initalSize, String name, FunctionMap<T, byte[]> encode, FunctionMap<byte[], T> decode) throws IOException
	{
		elementPositions = new ArrayList<Pair<Long,Integer>>(initalSize);
		temp = File.createTempFile("peakaboo - " + name , "list");
		raf = new RandomAccessFile(temp, "rw");
				
		this.encode = encode;
		this.decode = decode;
		
	}
	
	protected TempFileList(FunctionMap<T, byte[]> encode, FunctionMap<byte[], T> decode, File temp, RandomAccessFile raf, List<Pair<Long, Integer>> positions) throws IOException
	{
		elementPositions = positions;
		this.temp = temp;
		this.raf = raf;
		
		this.encode = encode;
		this.decode = decode;
		
	}
	
	
	
	public synchronized boolean add(T e)
	{
		try{
			long currentLength = raf.length();
			byte[] encoded = encode.f(e);
			long newLength = currentLength + encoded.length;
			
			raf.seek(currentLength);
			//raf.setLength(newLength);
			raf.write(encoded);
			
			elementPositions.add(new Pair<Long, Integer>(currentLength, encoded.length));
			
			return true;
			
		} catch (IOException ex)
		{
			throw new UnsupportedOperationException("Cannot write to backend file");
		}
		
	}
	
	public synchronized void add(int index, T element)
	{
		try{
			long currentLength = raf.length();
			byte[] encoded = encode.f(element);
			long newLength = currentLength + encoded.length;
			
			raf.seek(currentLength);
			//raf.setLength(newLength);
			raf.write(encoded);
			
			elementPositions.add(index, new Pair<Long, Integer>(currentLength, encoded.length));
			
		} catch (IOException ex)
		{
			ex.printStackTrace();
			
			throw new UnsupportedOperationException("Cannot write to backend file");
		}
	}

	public boolean addAll(Collection<? extends T> c)
	{
		for (T t : c)
		{
			add(t);
		}
		return true;
	}

	public boolean addAll(int index, Collection<? extends T> c)
	{
		for (T t : c)
		{
			add(index, t);
			index++;
		}
		return true;
	}

	public void clear()
	{
		elementPositions.clear();
	}

	public boolean contains(Object o)
	{
		for (T t : this)
		{
			if (t.equals(o)) return true;
		}
		return false;
	}

	public boolean containsAll(Collection<?> c)
	{
		boolean all = true;
		for (Object o : c)
		{
			all &= contains(o);
			if (!all) return all;
		}
		return all;
	}

	public synchronized T get(int index)
	{
		
		if (index == lastElementIndex) return lastElement;
				
		Pair<Long, Integer> position = elementPositions.get(index);
		long offset = position.first;
		int length = position.second;
		
		byte[] data = new byte[length];
		try
		{
			raf.seek(offset);
			raf.read(data, 0, length);
			
			lastElementIndex = index;
			lastElement = decode.f(data);
			
			return lastElement;
		}
		catch (IOException e)
		{
			return null;
		}
		
	}

	public int indexOf(Object o)
	{
		int index = 0;
		for (T t : this)
		{
			if (t == null && o == null) return index;
			if (t != null && t.equals(o)) return index;
			index++;
		}
		return -1;
	}

	public boolean isEmpty()
	{
		return elementPositions.isEmpty();
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
				return TempFileList.this.get(index++);
			}

			public void remove()
			{
				TempFileList.this.remove(index);
			}};
	}

	public int lastIndexOf(Object o)
	{
		T t;
		for (int i = size(); i >= 0; i--)
		{
			t = get(i);
			if (t.equals(o)) return i;
		}
		return -1;
	}

	public ListIterator<T> listIterator()
	{
		return listIterator(0);
	}

	public ListIterator<T> listIterator(final int startIndex)
	{
		return new ListIterator<T>() {

			int inext = startIndex;
			int lastReturned = startIndex;
			
			public void add(T t)
			{
				TempFileList.this.add(lastReturned, t);
			}

			public boolean hasNext()
			{
				return inext < elementPositions.size();
			}

			public boolean hasPrevious()
			{
				return inext > 0;
			}

			public T next()
			{
				lastReturned = inext;
				return TempFileList.this.get(inext++);
			}

			public int nextIndex()
			{
				return inext;
			}

			public T previous()
			{
				lastReturned = inext-1;
				return TempFileList.this.get(--inext);
			}

			public int previousIndex()
			{
				return inext-1;
			}

			public void remove()
			{
				TempFileList.this.remove(lastReturned);
				inext--;
			}

			public void set(T t)
			{
				TempFileList.this.set(lastReturned, t);
			}};
	}

	public boolean remove(Object o)
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

	public T remove(int index)
	{
		T t = get(index);
		elementPositions.remove(index);
		return t;
	}

	public boolean removeAll(Collection<?> c)
	{
		for (Object t : c)
		{
			remove(t);
		}
		return true;
	}

	public boolean retainAll(Collection<?> c)
	{
		ListIterator<T> li = listIterator();
		
		boolean modified = false;
		while(li.hasNext())
		{
			if ( ! c.contains(li.next()) )
			{
				li.remove();
				modified = true;
			}
		}
		
		return modified;
		
	}

	public synchronized T set(int index, T element)
	{
		try{
			
			T old = null;
			//old = get(index);
			
			long currentLength = raf.length();
			byte[] encoded = encode.f(element);
			long newLength = currentLength + encoded.length;
			
			raf.seek(currentLength);
			//raf.setLength(newLength);
			raf.write(encoded);
			
			elementPositions.set(index, new Pair<Long, Integer>(currentLength, encoded.length));
			
			return old;
			
		} catch (IOException ex)
		{
			throw new UnsupportedOperationException("Cannot write to backend file");
		}
	}

	public int size()
	{
		return elementPositions.size();
	}

	public List<T> subList(int fromIndex, int toIndex)
	{
		try
		{
			return new TempFileList<T>(encode, decode, temp, raf, elementPositions.subList(fromIndex, toIndex));
		}
		catch (IOException e)
		{
			return null;
		}
	}

	public Object[] toArray()
	{
		Object[] t = new Object[size()];
		for (int i = 0; i < size(); i++)
		{
			t[i] = get(i);
		}
		return t;
		
	}

	public <T> T[] toArray(T[] a)
	{
		T[] t;
		
		if (a.length >= size())
		{
			t = a;
		} else {
			t = (T[])(new Object[size()]);
		}

		for (int i = 0; i < size(); i++)
		{
			t[i] = (T) get(i);
		}
		
		
		return t;
	}
	
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
