package org.peakaboo.framework.cyclops;

import java.util.Iterator;
import java.util.stream.Stream;

public class IReadOnlySpectrum implements ReadOnlySpectrum {

	ReadOnlySpectrum backer;
	
	public IReadOnlySpectrum(ReadOnlySpectrum backer) {
		this.backer = backer;
	}

	public float get(int i) {
		return backer.get(i);
	}

	public int size() {
		return backer.size();
	}

	public float[] backingArrayCopy() {
		return backer.backingArrayCopy();
	}

	public ReadOnlySpectrum subSpectrum(int start, int stop) {
		return backer.subSpectrum(start, stop);
	}

	public Stream<Float> stream() {
		return backer.stream();
	}

	public Iterator<Float> iterator() {
		return backer.iterator();
	}

	public int hashCode() {
		return backer.hashCode();
	}

	public boolean equals(Object oother) {
		return backer.equals(oother);
	}

	public String toString() {
		return backer.toString();
	}
	
	public float sum() {
		return backer.sum();
	}

	@Override
	public float max() {
		return backer.max();
	}

	@Override
	public float min() {
		return backer.min();
	}
	
	
}
