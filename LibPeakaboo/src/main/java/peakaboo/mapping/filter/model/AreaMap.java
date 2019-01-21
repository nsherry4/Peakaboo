package peakaboo.mapping.filter.model;

import cyclops.Coord;
import cyclops.ReadOnlySpectrum;

public class AreaMap {

	private ReadOnlySpectrum data;
	private Coord<Integer> size;
	
	public AreaMap(ReadOnlySpectrum data, Coord<Integer> size) {
		this.data = data;
		this.size = size;
	}

	public ReadOnlySpectrum getData() {
		return data;
	}

	public Coord<Integer> getSize() {
		return size;
	}
	
	
	
}
