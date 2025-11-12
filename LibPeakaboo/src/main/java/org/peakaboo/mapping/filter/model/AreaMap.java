package org.peakaboo.mapping.filter.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.framework.accent.numeric.Bounds;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

public class AreaMap {

	private SpectrumView data;
	private Coord<Integer> size;
	//list is immutable
	private List<Element> elements;
	
	private Coord<Bounds<Number>> realDimensions;
	
	
	public AreaMap(SpectrumView data, Element e, Coord<Integer> size, Coord<Bounds<Number>> realDims) {
		this(data, Collections.singletonList(e), size, realDims);
	}
	public AreaMap(SpectrumView data, List<Element> elements, Coord<Integer> size, Coord<Bounds<Number>> realDims) {
		this.data = data;
		this.size = size;
		this.realDimensions = realDims;
		this.elements = Collections.unmodifiableList(new ArrayList<>(elements));
	}


	/**
	 * Copy constructor which keeps the metadata but replaces the actual map data
	 */
	public AreaMap(SpectrumView data, AreaMap other) {
		this(other);
		this.data = new ArraySpectrum(data);
	}
	
	/**
	 * Complate copy constructor
	 */
	public AreaMap(AreaMap other) {
		this.data = new ArraySpectrum(other.data);
		this.size = other.size;
		this.realDimensions = other.realDimensions;
		this.elements = other.elements;
	}
	
	public SpectrumView getData() {
		return data;
	}

	public Coord<Integer> getSize() {
		return size;
	}	
	
	public Coord<Bounds<Number>> getRealDimensions() {
		return realDimensions;
	}
	
	/**
	 * Returns an immutable list of elemental maps present in this area map. The list may be empty.
	 */
	public List<Element> getElements() {
		return Collections.unmodifiableList(elements);
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
		AreaMap firstSource = maps.iterator().next();
		return new AreaMap(sumSpectrum(maps), firstSource);
	}	
	
	public static Spectrum sumSpectrum(Iterable<AreaMap> maps) {
		if (!maps.iterator().hasNext()) { return null; }
		Coord<Integer> size = maps.iterator().next().getSize();
		Spectrum target = new ArraySpectrum(size.x * size.y);
		for (AreaMap map : maps) {
			SpectrumCalculations.addLists_inplace(target, map.getData());
		}
		return target;
	}
	
	
}
