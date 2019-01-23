package peakaboo.mapping.filter.model;

import java.util.Arrays;
import java.util.List;

import cyclops.Coord;
import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import cyclops.SpectrumCalculations;

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
	
	public static AreaMap sum(AreaMap... maps) {
		return sum(Arrays.asList(maps));
	}
	
	public static AreaMap sum(List<AreaMap> maps) {
		Coord<Integer> size = maps.get(0).getSize();
		return new AreaMap(sumSpectrum(maps), size);
	}
	
	
	public static Spectrum sumSpectrum(List<AreaMap> maps) {
		Coord<Integer> size = maps.get(0).getSize();
		Spectrum target = new ISpectrum(size.x * size.y);
		for (AreaMap map : maps) {
			SpectrumCalculations.addLists_inplace(target, map.getData());
		}
		return target;
	}
	
	
}
