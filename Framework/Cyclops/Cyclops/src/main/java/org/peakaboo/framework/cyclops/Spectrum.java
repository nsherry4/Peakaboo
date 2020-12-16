package org.peakaboo.framework.cyclops;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface Spectrum extends ReadOnlySpectrum {

	/**
	 * Copies the values from the given spectrum into this one. 
	 * Values copied will be in the range of 0 .. min(size(), s.size()) exclusive
	 * @param s
	 */
	void copy(ReadOnlySpectrum s);

	/**
	 * Copies the values from the given spectrum into this one. 
	 * Values copied will be in the range of 0 .. min(size(), s.size()) exclusive
	 * @param s
	 */
	void copy(Spectrum s);
	
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
	boolean add(float value);

	
	/**
	 * Sets the value of the entry at index i.
	 * @param i
	 * @param value
	 */
	void set(int i, float value);

	/**
	 * Return the array which is backing this Spectrum. This method does not return a copy, 
	 * but the real array. Modifying the contents of this array will modify the contents 
	 * of the Spectrum.
	 */
	float[] backingArray();

	void map_i(Function<Float, Float> f);
	
	//narrow the return type
	Spectrum subSpectrum(int start, int stop);

	/**
	 * Populate all entries in a spectrum with 0s
	 * This will not impact the add cursor's location
	 */
	void zero();
	
	
	/**
	 * Converts a list of integers (points) to a spectrum with matching indices set to 1
	 */
	static Spectrum fromPoints(List<Integer> points, int size) {
		return fromPoints(points, size, 1f);
	}
	/**
	 * Converts a list of integers (points) to a spectrum with matching indices set to a given value
	 */
	static Spectrum fromPoints(List<Integer> points, int size, float value) {
		Spectrum mask = new ISpectrum(size);
		for (int i = 0; i < size; i++) {
			if (points.contains(i)) {
				mask.add(value);
			} else {
				mask.add(0f);
			}
		}
		return mask;
	}
	
	/**
	 * Converts a spectrum to a list of integers (points) with entries for each non-zero element
	 */
	static List<Integer> toPoints(ReadOnlySpectrum spectrum) {
		List<Integer> points = new ArrayList<>();
		for (int i = 0; i < spectrum.size(); i++) {
			if (spectrum.get(i) != 0) {
				points.add(i);
			}
		}
		return points;
	}

}