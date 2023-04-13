package org.peakaboo.display.map.modes.correlation;

import java.util.Optional;

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
	public Optional<CoordInfo> getCoordInfo(Coord<Integer> coord) {
		if (isPointInBounds(coord)) {
			return Optional.of(new CoordInfo(-1, coord.x+1, coord.y+1, getValueAtCoord(coord)));
		} else {
			return Optional.empty();
		}
	}
	
	public Spectrum getData() {
		return data;
	}
	
	
}