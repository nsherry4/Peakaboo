package cyclops;

import java.io.Serializable;
import java.util.Iterator;
import java.util.stream.Stream;

public interface ReadOnlySpectrum extends Serializable, Iterable<Float> {

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
	 * Returns a new Spectrum containing a copy of the data for a subsection of this spectrum.
	 * @param start
	 * @param stop
	 */
	ReadOnlySpectrum subSpectrum(int start, int stop);

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
	
	
	
	default float sum() {
		float sum = 0;
		for (Float f : this) {
			sum += f;
		}
		return sum;
	}
	
	default float max() {
		float max = this.get(0);
		for (int i = 0; i < this.size(); i++) {
			float val = this.get(i);
			max = Math.max(max, val);
		}
		return max;		
	}
	
	default float min() {
		float min = this.get(0);
		for (int i = 0; i < this.size(); i++) {
			float val = this.get(i);
			min = Math.min(min, val);
		}
		return min;		
	}

}