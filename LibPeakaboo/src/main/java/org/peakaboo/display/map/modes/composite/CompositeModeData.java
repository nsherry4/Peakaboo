package org.peakaboo.display.map.modes.composite;

import org.peakaboo.display.map.modes.MapModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.Spectrum;

public class CompositeModeData implements MapModeData {

	private Coord<Integer> size;
	private Spectrum data;
	
	public CompositeModeData(Spectrum data, Coord<Integer> size) {
		this.data = data;
		this.size = size;	
	}
	
	@Override
	public Coord<Integer> getSize() {
		return size;
	}

	@Override
	public String getValueAtCoord(Coord<Integer> coord) {
		if (!isValidPoint(coord)) {
			return "-";
		}
		int index = getIndex(coord);
		if (index >= data.size()) return "-";
		return "" + SigDigits.roundFloatTo(data.get(index), 2);

	}

	public Spectrum getData() {
		return data;
	}
	
}
