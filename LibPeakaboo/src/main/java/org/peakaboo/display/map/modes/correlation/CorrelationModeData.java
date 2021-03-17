package org.peakaboo.display.map.modes.correlation;

import org.peakaboo.display.map.modes.MapModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public class CorrelationModeData implements MapModeData {
	public Spectrum data;
	public String xAxisTitle, yAxisTitle;
	public float xMaxCounts, yMaxCounts;
	private Coord<Integer> size;
	
	public CorrelationModeData(int bins) {
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
	
	@Override
	public String getInfoAtCoord(Coord<Integer> coord) {
		String noValue = "X: -, Y: -, Value: -";
		
		if (isPointInBounds(coord)) {
			return "X: " + (coord.x+1) + ", Y: " + (coord.y+1) + ", Value: " + getValueAtCoord(coord);
		} else {
			return noValue;
		}
		
	}
	
	public Spectrum getData() {
		return data;
	}
	
	
}