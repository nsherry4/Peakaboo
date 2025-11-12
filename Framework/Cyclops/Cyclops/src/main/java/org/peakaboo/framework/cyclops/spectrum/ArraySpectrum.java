package org.peakaboo.framework.cyclops.spectrum;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A high-performance, array-backed implementation of the Spectrum interface.
 * 
 * <p>ArraySpectrum provides fast access to float data while supporting both 
 * pre-allocated arrays and dynamic addition of elements via a cursor-based system.
 * The underlying data is stored in a fixed-size float array, making this implementation
 * ideal for scenarios requiring high-speed numerical operations on spectral data.
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li><b>High Performance:</b> Direct array access with minimal overhead</li>
 *   <li><b>Flexible Construction:</b> Create from arrays, lists, other spectra, or as empty containers</li>
 *   <li><b>Cursor-based Addition:</b> Add elements sequentially while tracking insertion position</li>
 * </ul>
 * 
 * <h3>Usage Patterns:</h3>
 * <pre>{@code
 * // Create empty spectrum and populate
 * ArraySpectrum spectrum = new ArraySpectrum(1000);
 * for (float value : measurements) {
 *     spectrum.add(value);
 * }
 * 
 * // Create from existing data
 * float[] data = {1.0f, 2.5f, 3.2f};
 * ArraySpectrum spectrum = new ArraySpectrum(data);
 * 
 * // Mathematical operations
 * float total = spectrum.sum();
 * float peak = spectrum.max();
 * }</pre>
 * 
 * <h3>Thread Safety:</h3>
 * <p>This class is <b>not thread-safe</b>. External synchronization is required 
 * when accessed from multiple threads concurrently.
 * 
 * @author Peakaboo Development Team
 * @see Spectrum
 * @see SpectrumView
 */
public class ArraySpectrum implements Spectrum {

	private float	data[];
	private int 	cursor;


	// No empty constructor access
	private ArraySpectrum() {}
	
	/**
	 * Creates a new Spectrum with the given size
	 * @param size the number of elements this spectrum can hold (must be >= 1)
	 * @throws IllegalArgumentException if size < 1
	 */
	public ArraySpectrum(int size) {
		if (size < 1) {
			throw new IllegalArgumentException("ArraySpectrum size must be >= 1");
		}
		this.data = new float[size];
		cursor = -1;
	}
	
	/**
	 * Creates a new Spectrum of the given size, with all values set to the given value
	 * The cursor will be set to the end of the new ArraySpectrum.
	 * @param size the number of elements this spectrum can hold (must be >= 1)
	 * @param initialize the value to set all elements to
	 * @throws IllegalArgumentException if size < 1
	 */
	public ArraySpectrum(int size, float initialize) {
		if (size < 1) {
			throw new IllegalArgumentException("ArraySpectrum size must be >= 1");
		}
		this.data = new float[size];
		cursor = -1;
		
		Arrays.fill(this.data, 0, size, initialize);
		cursor = size - 1;
	}
	
	/**
	 * Creates a new Spectrum based on a copy of the given array.
	 * The cursor will be set to the end of the new ArraySpectrum.
	 * @param fromArray the source array to copy from (cannot be null)
	 * @throws IllegalArgumentException if fromArray is null
	 */
	public ArraySpectrum(float[] fromArray) {
		this(fromArray, true);
	}
	
	/**
	 * Creates a new Spectrum based on the given array. If copy is 
	 * false, the Spectrum will be backed directly by the given array.
	 * The cursor will be set to the end of the new ArraySpectrum.
	 * @param fromArray the source array to use (cannot be null)
	 * @param copy if true, creates a copy; if false, uses the array directly
	 * @throws IllegalArgumentException if fromArray is null
	 */
    public ArraySpectrum(float[] fromArray, boolean copy) {
		if (fromArray == null) {
			throw new IllegalArgumentException("Source array cannot be null");
		}
		if (fromArray.length < 1) {
			throw new IllegalArgumentException("ArraySpectrum size must be >= 1");
		}
    	if (copy) {
    		this.data = Arrays.copyOf(fromArray, fromArray.length);
    	} else {
    		this.data = fromArray;
    	}
		cursor = this.data.length - 1;
    }

    
	/**
	 * Creates a new Spectrum based on a copy of the given array.
	 * The values in the given array will be converted to floats.
	 * The cursor will be set to the end of the new ArraySpectrum.
	 * @param fromArray the source double array to convert and copy (cannot be null)
	 * @throws IllegalArgumentException if fromArray is null
	 */
	public ArraySpectrum(double[] fromArray) {
		if (fromArray == null) {
			throw new IllegalArgumentException("Source array cannot be null");
		}
		if (fromArray.length < 1) {
			throw new IllegalArgumentException("ArraySpectrum size must be >= 1");
		}
        this.data = new float[fromArray.length];
        for (int i = 0; i < this.data.length; i++) {
            data[i] = (float)fromArray[i];
        }
        cursor = this.data.length - 1;
	}

	/**
	 * Creates a new Spectrum based on the values in the given list.
	 * The cursor will be set to the end of the new ArraySpectrum.
	 * @param fromList the source list of Float values to copy (cannot be null)
	 * @throws IllegalArgumentException if fromList is null
	 */
	public ArraySpectrum(List<Float> fromList) {
		if (fromList == null) {
			throw new IllegalArgumentException("Source list cannot be null");
		}
		if (fromList.size() < 1) {
			throw new IllegalArgumentException("ArraySpectrum size must be >= 1");
		}
		this.data = new float[fromList.size()];
		for (int i = 0; i < this.data.length; i++) {
			data[i] = fromList.get(i);
		}
		cursor = this.data.length - 1;
	}
	
	/**
	 * Creates a new spectrum copied from the given spectrum.
	 * The cursor will be set to the end of the new ArraySpectrum.
	 * @param copy the source spectrum to copy from (cannot be null)
	 * @throws IllegalArgumentException if copy is null
	 */
	public ArraySpectrum(SpectrumView copy) {
		if (copy == null) {
			throw new IllegalArgumentException("Source spectrum cannot be null");
		}
		if (copy.size() < 1) {
			throw new IllegalArgumentException("ArraySpectrum size must be >= 1");
		}
		if (copy instanceof ArraySpectrum source) {
			this.data = source.backingArrayCopy();
			this.cursor = source.cursor;
		} else {
			this.data = copy.backingArrayCopy();
			this.cursor = this.data.length - 1;
		}
	}
	
	

	@Override
	public void copy(SpectrumView s) {
		if (s.size() == 0 || this.size() == 0) return; // Handle empty spectra
		
		int copyLength = Math.min(this.size(), s.size());
		if (s instanceof Spectrum spectrum) {
			copy(spectrum.backingArray(), 0, copyLength - 1);
		} else {
			copy(s.backingArrayCopy(), 0, copyLength - 1);
		}
	}
	
	/**
	 * Copies the values from the given spectrum into this one. 
	 * @param s the source spectrum to copy from
	 * @param first the first index to copy
	 * @param last the last index to copy (inclusive)
	 */
	@Override
	public void copy(Spectrum s, int first, int last) {
		copy(s.backingArray(), first, last);
	}
	
	/**
	 * Copies the given array into this Spectrum's data array 
	 * @param array the array to copy from
	 * @param first the first index to copy from source
	 * @param last the last index to copy from source (inclusive)
	 */
	private void copy(float[] array, int first, int last) {
		if (array == null) {
			throw new IllegalArgumentException("Source array cannot be null");
		}
		if (first < 0 || last >= array.length || first > last) {
			throw new IllegalArgumentException("Invalid source range: [" + first + ", " + last + "] for array length " + array.length);
		}
		if (last >= this.data.length) {
			throw new IllegalArgumentException("Copy range [" + first + ", " + last + "] exceeds destination size " + this.data.length);
		}
		
		System.arraycopy(array, first, this.data, first, last - first + 1);
		cursor = Math.max(cursor, last);
	}

	/**
	 * Adds a value to the Spectrum.  When a new spectrum is created 
	 * without being initialized with any values, it can have <tt>size</tt> 
	 * new values added to it. These values are added in order, just 
	 * as with {@link List#add(Object)}. The add method will return true
	 * if any new value was added to the spectrum, or false if there was
	 * no more space available. Calling {@link ArraySpectrum#set(int, float)}
	 * does not have any effect on the add method
	 * @param value the float value to add to the spectrum
	 * @return true if the value was added, false if the spectrum is full
	 */
	@Override
	public boolean add(float value) {
		if (cursor < this.data.length - 1) {
			data[++cursor] = value;
			return true;
		} else {
			return false;
		}
	}


	/**
	 * Sets the value of the entry at index i.
	 * @param i the index to set the value at
	 * @param value the new float value to set
	 */
	@Override
	public void set(int i, float value) {
		data[i] = value;
	}


	/**
	 * Gets the value of the entry at index i
	 * @param i the index to retrieve the value from
	 * @return the float value at the specified index
	 */
	@Override
	public float get(int i) {
		return data[i];
	}


	/**
	 * Gets the size of this Spectrum
	 * @return the number of elements in this spectrum
	 */
	@Override
	public int size() {
		return this.data.length;
	}


	/**
	 * Returns a copy of the data as an array
	 * @return a new float array containing a copy of this spectrum's data
	 */
	@Override
	public float[] backingArrayCopy() {
		return Arrays.copyOf(data, this.data.length);
	}

	/**
	 * Returns a new Spectrum containing a copy of the data for a subsection of this spectrum.
	 * @param start the starting index (inclusive)
	 * @param stop the ending index (inclusive)
	 * @return a new ArraySpectrum containing the copied subsection
	 */
	@Override
	public ArraySpectrum subSpectrum(int start, int stop) {
		int length = stop - start + 1;
		ArraySpectrum target = new ArraySpectrum(length);
		System.arraycopy(data, start, target.data, 0, length);
		target.cursor = length-1;
		return target;
	}
	
	/**
	 * Return the array which is backing this Spectrum. This method does not return a copy, 
	 * but the real array. Modifying the contents of this array will modify the contents 
	 * of the Spectrum.
	 * @return the internal float array backing this spectrum (not a copy)
	 * @apiNote Use with caution - direct modifications affect the spectrum
	 */
	@Override
	public float[] backingArray() {
		return data;
	}
	

	/**
	 * Return a stream accessing the backing array
	 * @return a Stream of Float values for this spectrum's data
	 */
	@Override
	public Stream<Float> stream() {
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
	}


	/**
	 * Returns an iterator over {@link Float} values for this Spectrum.
	 * Note: The iterator's remove() method throws UnsupportedOperationException.
	 * @return an iterator over the Float values in this spectrum
	 */
	@Override
	public Iterator<Float> iterator() {
		
		return new Iterator<Float>() {

			int	index	= 0;

			@Override
			public boolean hasNext()
			{
				return (index < size());
			}

			@Override
			public Float next()
			{
				return data[index++];
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException("Remove not supported by ArraySpectrum");
			}
		};
	}
	
	/**
	 * Hash code based on array contents using Java's standard approach for float arrays.
	 * For performance, only samples elements if array is very large.
	 */
	@Override
	public int hashCode() {
		
		final int PRIME = 31; // Standard prime multiplier for hash distribution
		int size = this.data.length;
		
		if (this.data.length <= 200) {
			// For smaller arrays, hash all elements
			return Arrays.hashCode(Arrays.copyOf(data, size));
		} else {
			// For large arrays, sample strategically to balance performance and distribution
			int result = 1;
			int step = size / 100; // Sample ~100 elements
			for (int i = 0; i < size; i += step) {
				result = PRIME * result + Float.floatToIntBits(data[i]);
			}
			// Always include first, last, and middle elements
			result = PRIME * result + Float.floatToIntBits(data[0]);
			result = PRIME * result + Float.floatToIntBits(data[size-1]);
			result = PRIME * result + Float.floatToIntBits(data[size/2]);
			result = PRIME * result + size; // Include size in hash
			return result;
		}
	}
	
	@Override
	public boolean equals(Object oother) {
		if (oother instanceof ArraySpectrum other) {
			if (other.data.length != this.data.length) return false;
			for (int i = 0; i < this.data.length; i++) {
				if (other.data[i] != this.data[i]) return false;
			}
		} else {
			return false;
		}
		return true;
	}
	
	
	
	/**
	 * Returns a string representation of this spectrum with space-separated values.
	 * @return string representation with space-separated float values
	 */
	@Override
	public String toString() {
		return toString(" ");
	}
	
	/**
	 * Returns a string representation of this spectrum with custom delimiter.
	 * @param delimiter the string to use between float values
	 * @return string representation with specified delimiter between values
	 */
	@Override
	public String toString(String delimiter) {
		
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

	/**
	 * Sets all values in this spectrum to zero.
	 */
	@Override
	public void zero() {
		Arrays.fill(data, 0f);
	}

	/**
	 * Sets values in the specified range to zero.
	 * @param first the starting index (inclusive)
	 * @param last the ending index (inclusive)
	 */
	@Override
	public void zero(int first, int last) {
		Arrays.fill(data, first, last+1, 0f);
	}
	
	/**
	 * Calculates the sum of all values in this spectrum.
	 * @return the sum of all float values in the spectrum
	 */
	public float sum()  {
		float sum = 0;
		for (int i = 0; i < this.data.length; i++) {
			sum += data[i];
		}
		return sum;
	}
	
	/**
	 * Finds the maximum value in this spectrum.
	 * @return the largest float value in the spectrum
	 * @throws ArrayIndexOutOfBoundsException if the spectrum is empty
	 */
	public float max() {
		float max = data[0];
		for (int i = 0; i < this.data.length; i++) {
			float val = data[i];
			max = Math.max(max, val);
		}
		return max;		
	}
	
	/**
	 * Finds the minimum value in this spectrum.
	 * @return the smallest float value in the spectrum
	 * @throws ArrayIndexOutOfBoundsException if the spectrum is empty
	 */
	public float min() {
		float min = data[0];
		for (int i = 0; i < this.data.length; i++) {
			float val = data[i];
			min = Math.min(min, val);
		}
		return min;		
	}

	
}
