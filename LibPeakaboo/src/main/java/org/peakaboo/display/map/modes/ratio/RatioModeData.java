package org.peakaboo.display.map.modes.ratio;

import org.peakaboo.controller.mapper.fitting.modes.RatioModeController.Ratios;
import org.peakaboo.display.map.modes.MapModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public class RatioModeData implements MapModeData {

	private Coord<Integer> size;
	private Pair<Spectrum, Spectrum> data;
	private boolean relative;
	
	public RatioModeData(Pair<Spectrum, Spectrum> data, Coord<Integer> size, boolean relative) {
		this.size = size;
		this.data = data;
		this.relative = relative;
	}

	@Override
	public Coord<Integer> getSize() {
		return size;
	}

	@Override
	public String getValueAtCoord(Coord<Integer> coord) {
		if (!isPointInBounds(coord)) {
			return "-";
		}
		if (relative) return "--";
		
		int index = getIndex(coord);
		if (data.second.get(index) != 0) return "Invalid";
		return Ratios.fromFloat(  data.first.get(index)  );
	}

	public Pair<Spectrum, Spectrum> getData() {
		return data;
	}
	
}
