package org.peakaboo.display.map.modes.ratio;

import java.util.ArrayList;
import java.util.Optional;

import org.peakaboo.controller.mapper.fitting.modes.RatioModeController.Ratios;
import org.peakaboo.display.map.modes.MapModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
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

	@Override
	public Optional<SelectionInfo> getMapSelectionInfo() {
		var unselectable = new ArrayList<Integer>();
		Spectrum invalidMap = data.second;
		for (int i = 0; i < invalidMap.size(); i++) {
			if (invalidMap.get(i) > 0f) {
				unselectable.add(i);
			}
		}
		var ratios = new ArraySpectrum(data.first);
		return Optional.of(new SelectionInfo(ratios, unselectable));
	}
	
}
