package org.peakaboo.display.map.modes.ternary;

import org.peakaboo.display.map.modes.MapModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.Spectrum;

public class TernaryModeData implements MapModeData {

	public Spectrum data;
	public String xAxisTitle, yAxisTitle;
	public float xMaxCounts, yMaxCounts;
	private Coord<Integer> size;
	
	public TernaryModeData(int bins) {
		this.size = new Coord<>(bins, bins);
	}

	@Override
	public Coord<Integer> getSize() {
		return size;
	}

	@Override
	public String getValueAtCoord(Coord<Integer> coord) {
		
		if (isPointInBounds(coord)) {
			int index = getIndex(coord);
			float frequency = data.get(index);
			return "" + SigDigits.roundFloatTo(  frequency, 2  );
		} else {
			return "-";
		}

	}

	public Spectrum getData() {
		return data;
	}
	
}
