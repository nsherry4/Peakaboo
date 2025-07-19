package org.peakaboo.framework.cyclops.spectrum;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.peakaboo.framework.cyclops.log.CyclopsLog;

public class ArraySpectrum implements Spectrum
{

	private float	data[];
	private int		size;
	private int		maxIndex;


	/**
	 * Public constructor for (de)serialization purposes only.
	 */
	public ArraySpectrum()
	{
		
	}

	/**
	 * Creates a new Spectrum with the given size
	 * @param size
	 */
	public ArraySpectrum(int size)
	{
		this.data = new float[size];
		this.size = size;		
		maxIndex = 0 - 1;
	}
	
	/**
	 * Creates a new Spectrum of the given size, with all values set to the given value
	 * @param size
	 * @param initialize
	 */
	public ArraySpectrum(int size, float initialize)
	{
		this.data = new float[size];
		this.size = size;
		maxIndex = 0 - 1;
		
		Arrays.fill(this.data, 0, size, initialize);
		maxIndex = size - 1;
	}
	
	/**
	 * Creates a new Spectrum based on a copy of the given array
	 * @param fromArray
	 */
	public ArraySpectrum(float[] fromArray)
	{
		this(fromArray, true);
	}
	
	/**
	 * Creates a new Spectrum based on the given array. If copy is 
	 * false, the Spectrum will be backed directly by the given array.
	 * @param fromArray
	 * @param copy
	 */
    public ArraySpectrum(float[] fromArray, boolean copy)
    {
    	if(copy) {
    		this.data = Arrays.copyOf(fromArray, fromArray.length);
    		this.size = fromArray.length;
            maxIndex = size - 1;
    	} else {
    		this.data = fromArray;
    		this.size = fromArray.length;
    		maxIndex = size - 1;
    	}
    }

    
	/**
	 * Creates a new Spectrum based on a copy of the given array.
	 * The values in the given array will be converted to floats.
	 * @param fromArray
	 */
	public ArraySpectrum(double[] fromArray)
	{
        this.data = new float[fromArray.length];
        this.size = fromArray.length;

        for (int i = 0; i < size; i++)
        {
            data[i] = (float)fromArray[i];
        }
        maxIndex = size - 1;
	}

	/**
	 * Creates a new Spectrum based on the values in the given list.
	 * @param fromList
	 */
	public ArraySpectrum(List<Float> fromList)
	{
		this.data = new float[fromList.size()];
		this.size = fromList.size();

		for (int i = 0; i < size; i++)
		{
			data[i] = fromList.get(i);
		}
		maxIndex = size - 1;

	}


	/**
	 * Creates a new spectrum copied from the given spectrum
	 * @param copy
	 */
	public ArraySpectrum(SpectrumView copy)
	{
		if (copy instanceof ArraySpectrum source) {
			this.data = source.backingArrayCopy();
			this.size = source.size;
			this.maxIndex = source.maxIndex;
		}
				
	}
	
	

	@Override
	public void copy(SpectrumView s)
	{
		copy(((Spectrum) s));
	}


	@Override
	public void copy(Spectrum s)
	{
		copy(s.backingArray(), 0, Math.min(this.size, s.size()) - 1);
	}
	
	/**
	 * Copies the values from the given spectrum into this one. 
	 * @param s
	 * @param first the first index to copy
	 * @param last the last index to copy
	 */
	@Override
	public void copy(Spectrum s, int first, int last)
	{
		copy(s.backingArray(), first, last);
	}
	
	/**
	 * Copies the given array into the start of this Spectrum's data array 
	 * @param array the array to copy from
	 * @param first the first index to copy
	 * @param last the last index to copy
	 */
	private void copy(float[] array, int first, int last) {
		System.arraycopy(array, first, this.data, first, last - first + 1);
		maxIndex = Math.max(maxIndex, last);
	}

	/**
	 * Adds a value to the Spectrum.  When a new spectrum is created 
	 * without being initialized with any values, it can have <tt>size</tt> 
	 * new values added to it. These values are added in order, just 
	 * as with {@link List#add(Object)}. The add method will return true
	 * if any new value was added to the spectrum, or false if there was
	 * no more space available. Calling {@link ArraySpectrum#set(int, float)}
	 * does not have any effect on the add method
	 * @param value
	 */
	@Override
	public boolean add(float value)
	{
		if (maxIndex < size - 1)
		{
			data[++maxIndex] = value;
			return true;
		}
		else
		{
			return false;
		}
	}


	/**
	 * Sets the value of the entry at index i.
	 * @param i
	 * @param value
	 */
	@Override
	public void set(int i, float value)
	{
		data[i] = value;
	}


	/**
	 * Gets the value of the entry at index i
	 * @param i
	 */
	@Override
	public float get(int i)
	{
		return data[i];
	}


	/**
	 * Gets the size of this Spectrum
	 */
	@Override
	public int size()
	{
		return size;
	}


	/**
	 * Returns a copy of the data as an array
	 */
	@Override
	public float[] backingArrayCopy()
	{
		return Arrays.copyOf(data, data.length);
	}

	/**
	 * Returns a new Spectrum containing a copy of the data for a subsection of this spectrum.
	 * @param start
	 * @param stop
	 */
	@Override
	public ArraySpectrum subSpectrum(int start, int stop)
	{
		
		int length = stop - start + 1;
		ArraySpectrum target = new ArraySpectrum(length);
		System.arraycopy(data, start, target.data, 0, length);
		target.maxIndex = length-1;
		
		return target; 
	}
	
	/**
	 * Return the array which is backing this Spectrum. This method does not return a copy, 
	 * but the real array. Modifying the contents of this array will modify the contents 
	 * of the Spectrum.
	 */
	@Override
	public float[] backingArray()
	{
		return data;
	}
	

	/**
	 * Return a stream accessing the backing array
	 * @return
	 */
	@Override
	public Stream<Float> stream() 
	{
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
	}


	/**
	 * Returns an iterator over {@link Float} values for this Spectrum
	 */
	@Override
	public Iterator<Float> iterator()
	{
		return new Iterator<Float>() {

			int	index	= 0;

			@Override
			public boolean hasNext()
			{
				return (index < size);
			}

			@Override
			public Float next()
			{
				return data[index++];
			}

			@Override
			public void remove()
			{
				data[index] = 0.0f;
			}
		};
	}


	public static Function<ArraySpectrum, byte[]> getEncoder()
	{

		//Function to serialize a spectrum
		return s -> {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos;
			try
			{
				oos = new ObjectOutputStream(baos);
				oos.writeObject(s);
				oos.close();
				return baos.toByteArray();
			}
			catch (IOException e)
			{
				CyclopsLog.get().log(Level.SEVERE, "Failed to encode spectrum", e);
				return new byte[0];
			}

		};
	}

	public static Function<byte[], ArraySpectrum> getDecoder(){

		//Function to deserialize a spectrum

		return bs -> {
			ByteArrayInputStream bais = new ByteArrayInputStream(bs);
			ObjectInputStream ois;
			try
			{
				ois = new ObjectInputStream(bais);
				ArraySpectrum s = (ArraySpectrum) ois.readObject();
				ois.close();
				return s;
			}
			catch (IOException e)
			{
				CyclopsLog.get().log(Level.SEVERE, "Failed to decode spectrum", e);
				return null;
			}
			catch (ClassNotFoundException e)
			{
				CyclopsLog.get().log(Level.SEVERE, "Failed to decode spectrum", e);
				return null;
			}

		};

	}
	
	/**
	 * Hash code returns the integer sum of the first 10 (or less) elements
	 */
	@Override
	public int hashCode()
	{
		float sum = 0;
		for (int i = 0; i < 10; i++)
		{
			if (size() <= i) break;
			sum += get(i);
		}
		return (int)sum;
	}
	
	@Override
	public boolean equals(Object oother)
	{
		if (oother instanceof ArraySpectrum other) {
			if (other.size() != size()) return false;
			for (int i = 0; i < size(); i++) {
				if (other.get(i) != get(i)) return false;
			}
			
		} else {
			return false;
		}
		
		return true;
	}
	
	
	
	@Override
	public String toString() {
		return toString(" ");
	}
	
	@Override
	public String toString(String delimiter)
	{
		StringBuilder sb = new StringBuilder();
		boolean first = true;
				
		
		for (Float f : this)
		{
			if (first) {
				first = false;
			} else {
				sb.append(delimiter);
			}
			sb.append(f);
		}
				
		return sb.toString();
		
	}

	@Override
	public void zero() {
		Arrays.fill(data, 0f);
	}

	@Override
	public void zero(int first, int last) {
		Arrays.fill(data, first, last+1, 0f);
	}
	
	public float sum()  {
		float sum = 0;
		for (int i = 0; i < size; i++) {
			sum += data[i];
		}
		return sum;
	}
	
	public float max() {
		float max = this.get(0);
		for (int i = 0; i < size; i++) {
			float val = data[i];
			max = Math.max(max, val);
		}
		return max;		
	}
	
	public float min() {
		float min = data[0];
		for (int i = 0; i < size; i++) {
			float val = data[i];
			min = Math.min(min, val);
		}
		return min;		
	}

	
}
