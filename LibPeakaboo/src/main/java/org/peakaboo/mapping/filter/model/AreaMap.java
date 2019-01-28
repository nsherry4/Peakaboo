package org.peakaboo.mapping.filter.model;

import java.util.Arrays;
import java.util.List;

import cyclops.Bounds;
import cyclops.Coord;
import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import cyclops.SpectrumCalculations;

public class AreaMap {

	private ReadOnlySpectrum data;
	private Coord<Integer> size;
	
	Coord<Bounds<Number>> realDimensions;
	
	
	public AreaMap(ReadOnlySpectrum data, Coord<Integer> size, Coord<Bounds<Number>> realDims) {
		this.data = data;
		this.size = size;
		this.realDimensions = realDims;
	}

	public ReadOnlySpectrum getData() {
		return data;
	}

	public Coord<Integer> getSize() {
		return size;
	}	
	
	public Coord<Bounds<Number>> getRealDimensions() {
		return realDimensions;
	}
	
	
	

	public static AreaMap sum(AreaMap... maps) {
		return sum(Arrays.asList(maps));
	}
	
	public static AreaMap sum(List<AreaMap> maps) {
		if (maps.size() == 0) { return null; }
		Coord<Integer> size = maps.get(0).getSize();
		Coord<Bounds<Number>> realDimensions = maps.get(0).getRealDimensions();
		return new AreaMap(sumSpectrum(maps), size, realDimensions);
	}	
	
	public static Spectrum sumSpectrum(List<AreaMap> maps) {
		if (maps.size() == 0) { return null; }
		Coord<Integer> size = maps.get(0).getSize();
		Spectrum target = new ISpectrum(size.x * size.y);
		for (AreaMap map : maps) {
			SpectrumCalculations.addLists_inplace(target, map.getData());
		}
		return target;
	}
	
	
}
