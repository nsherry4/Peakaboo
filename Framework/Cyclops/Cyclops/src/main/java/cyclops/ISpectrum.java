package cyclops;

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

import cyclops.log.CyclopsLog;

public class ISpectrum implements Spectrum
{

	private float	data[];
	private int		size;
	private int		maxIndex;


	/**
	 * Public constructor for (de)serialization purposes only.
	 */
	public ISpectrum()
	{
		
	}

	/**
	 * Creates a new Spectrum with the given size
	 * @param size
	 */
	public ISpectrum(int size)
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
	public ISpectrum(int size, float initialize)
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
	public ISpectrum(float[] fromArray)
	{
		this(fromArray, true);
	}
	
	/**
	 * Creates a new Spectrum based on the given array. If copy is 
	 * false, the Spectrum will be backed directly by the given array.
	 * @param fromArray
	 * @param copy
	 */
    public ISpectrum(float[] fromArray, boolean copy)
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
	public ISpectrum(double[] fromArray)
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
	public ISpectrum(List<Float> fromList)
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
	public ISpectrum(ReadOnlySpectrum copy)
	{
		if (copy instanceof ISpectrum) {
			ISpectrum source = (ISpectrum) copy;
			this.data = source.backingArrayCopy();
			this.size = source.size;
			this.maxIndex = source.maxIndex;
		}
				
	}
	
	
	/**
	 * Copies the values from the given spectrum into this one. 
	 * Values copied will be in the range of 0 .. min(size(), s.size()) exclusive
	 * @param s
	 */
	@Override
	public void copy(ReadOnlySpectrum s)
	{
		copy(s.backingArrayCopy());
	}

	/**
	 * Copies the values from the given spectrum into this one. 
	 * Values copied will be in the range of 0 .. min(size(), s.size()) exclusive
	 * @param s
	 */
	@Override
	public void copy(Spectrum s)
	{
		copy(s.backingArray());
	}
	
	/**
	 * Copies the given array into the start of this Spectrum's data array 
	 * up to min(this.size(), array.length) exclusive;
	 * @param array the array to copy from
	 */
	private void copy(float[] array) {
		int length = Math.min(this.data.length, array.length);
		System.arraycopy(array, 0, this.data, 0, length);
		maxIndex = Math.max(maxIndex, array.length-1);
	}

	/**
	 * Adds a value to the Spectrum.  When a new spectrum is created 
	 * without being initialized with any values, it can have <tt>size</tt> 
	 * new values added to it. These values are added in order, just 
	 * as with {@link List#add(Object)}. The add method will return true
	 * if any new value was added to the spectrum, or false if there was
	 * no more space available. Calling {@link ISpectrum#set(int, float)}
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
	public ISpectrum subSpectrum(int start, int stop)
	{
		
		int length = stop - start + 1;
		ISpectrum target = new ISpectrum(length);
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

			public boolean hasNext()
			{
				return (index < size);
			}


			public Float next()
			{
				return data[index++];
			}


			public void remove()
			{
				data[index] = 0.0f;
			}
		};
	}

		
	@Override
	public void map_i(Function<Float, Float> f)
	{
		for (int i = 0; i < size; i++)
		{
			set(i, f.apply(data[i]));
		}
	}
	

	public static Function<ISpectrum, byte[]> getEncoder()
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

	public static Function<byte[], ISpectrum> getDecoder(){

		//Function to deserialize a spectrum

		return bs -> {
			ByteArrayInputStream bais = new ByteArrayInputStream(bs);
			ObjectInputStream ois;
			try
			{
				ois = new ObjectInputStream(bais);
				ISpectrum s = (ISpectrum) ois.readObject();
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
		if (oother instanceof ISpectrum) {
			
			Spectrum other = (Spectrum)oother;
			
			if (other.size() != size()) return false;
			
			for (int i = 0; i < size(); i++) if (other.get(i) != get(i)) return false;
			
		} else {
			return false;
		}
		
		return true;
	}
	
	
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		boolean first = true;
				
		
		for (Float f : this)
		{
			if (first) {
				first = false;
			} else {
				sb.append(" ");
			}
			sb.append(f);
		}
				
		return sb.toString();
		
	}

	@Override
	public void zero() {
		Arrays.fill(data, 0f);
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
