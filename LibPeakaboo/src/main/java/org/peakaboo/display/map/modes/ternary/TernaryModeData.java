package org.peakaboo.display.map.modes.ternary;

import java.util.List;
import java.util.Optional;

import org.peakaboo.display.map.modes.MapModeData;
import org.peakaboo.display.map.modes.MapModeData.CoordInfo;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public class TernaryModeData implements MapModeData {

	public Spectrum data;
	public String xCornerTitle, yCornerTitle, oCornerTitle;
	public float xMaxCounts, yMaxCounts;
	public List<Integer> unselectables;
	private Coord<Integer> size;
	
	
	public TernaryModeData(int bins) {
		this.size = new Coord<>(bins, bins);
	}

	@Override
	public Coord<Integer> getSize() {
		return size;
	}

	//Don't report index values since this is not a spatial map mode
	@Override
	public Optional<CoordInfo> getCoordInfo(Coord<Integer> coord) {
		if (isPointInBounds(coord)) {
			return Optional.of(new CoordInfo(-1, coord.x+1, coord.y+1, getValueAtCoord(coord)));
		} else {
			return Optional.empty();
		}
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
