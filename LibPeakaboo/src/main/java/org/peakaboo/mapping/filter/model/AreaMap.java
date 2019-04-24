package org.peakaboo.mapping.filter.model;

import java.util.Arrays;
import java.util.List;

import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.framework.cyclops.SpectrumCalculations;

public class AreaMap {

	private ReadOnlySpectrum data;
	private Coord<Integer> size;
	
	private Coord<Bounds<Number>> realDimensions;
	
	
	public AreaMap(ReadOnlySpectrum data, Coord<Integer> size, Coord<Bounds<Number>> realDims) {
		this.data = data;
		this.size = size;
		this.realDimensions = realDims;
	}

	public AreaMap(AreaMap other) {
		this.data = new ISpectrum(other.data);
		this.size = other.size;
		this.realDimensions = other.realDimensions;
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
	
	public void add(AreaMap other) {
		if (!other.size.equals(size)) {
			throw new IllegalArgumentException("Size mismatch");
		}
		this.data = SpectrumCalculations.addLists(this.data, other.data);
	}
	

	
	
	public static AreaMap sum(AreaMap... maps) {
		return sum(Arrays.asList(maps));
	}
	
	public static AreaMap sum(Iterable<AreaMap> maps) {
		if (!maps.iterator().hasNext()) { return null; }
		Coord<Integer> size = maps.iterator().next().getSize();
		Coord<Bounds<Number>> realDimensions = maps.iterator().next().getRealDimensions();
		return new AreaMap(sumSpectrum(maps), size, realDimensions);
	}	
	
	public static Spectrum sumSpectrum(Iterable<AreaMap> maps) {
		if (!maps.iterator().hasNext()) { return null; }
		Coord<Integer> size = maps.iterator().next().getSize();
		Spectrum target = new ISpectrum(size.x * size.y);
		for (AreaMap map : maps) {
			SpectrumCalculations.addLists_inplace(target, map.getData());
		}
		return target;
	}
	
	
}
