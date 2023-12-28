package org.peakaboo.framework.cyclops.spectrum;

import java.io.Serializable;
import java.util.Iterator;
import java.util.stream.Stream;

public interface SpectrumView extends Serializable, Iterable<Float> {

	/**
	 * Gets the value of the entry at index i
	 * @param i
	 */
	float get(int i);

	/**
	 * Gets the size of this Spectrum
	 */
	int size();

	default boolean inBounds(int index) {
		if (index < 0) return false;
		if (index >= size()) return false;
		return true;
	}
	
	/**
	 * Returns a copy of the data as an array
	 */
	float[] backingArrayCopy();

	/**
	 * Returns a new SpectrumView containing a copy of the data for a subsection of this spectrum.
	 * @param start
	 * @param stop
	 */
	SpectrumView subSpectrum(int start, int stop);

	/**
	 * Return a stream accessing the backing array. Note that stream processing tends to be 
	 * considerably slower than looping over the Spectrum.
	 * @return
	 */
	Stream<Float> stream();

	/**
	 * Returns an iterator over {@link Float} values for this Spectrum
	 */
	Iterator<Float> iterator();


	/**
	 * Hash code returns the integer sum of the first 10 (or less) elements
	 */
	int hashCode();

	boolean equals(Object oother);

	String toString();
	String toString(String delimiter);
	
	
	
	float sum();
	float max();
	float min();

}