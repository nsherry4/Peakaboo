package org.peakaboo.display.map.modes.composite;

import java.util.List;
import java.util.Optional;

import org.peakaboo.display.map.modes.MapModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public class CompositeModeData implements MapModeData {

	private Coord<Integer> size;
	private Spectrum data;
	private List<Integer> invalidPoints;
	private Spectrum invalidMask;
	
	public CompositeModeData(Spectrum data, Coord<Integer> size, List<Integer> invalidPoints) {
		this.data = data;
		this.size = size;	
		this.invalidPoints = invalidPoints;
		//build invalid point mask from list of invalid points
		invalidMask = Spectrum.fromPoints(invalidPoints, data.size());
	}
	
	@Override
	public Coord<Integer> getSize() {
		return size;
	}

	@Override
	public String getValueAtCoord(Coord<Integer> coord) {
		if (!isPointInBounds(coord) || !hasBackingData(coord)) {
			return "-";
		}
		int index = getIndex(coord);
		if (index >= data.size()) return "-";
		return "" + SigDigits.roundFloatTo(data.get(index), 2);

	}

	public Spectrum getData() {
		return data;
	}
	
	private boolean hasBackingData(Coord<Integer> coord) {
		int index = getIndex(coord);
		if (index < 0) { return false; }
		return !invalidPoints.contains(index);
	}

	public Spectrum getInvalidPoints() {
		return invalidMask;
	}

	@Override
	public Optional<SelectionInfo> getMapSelectionInfo() {
		return Optional.of(new SelectionInfo(getData(), IntArrayList.of()));
	}
	
	
}
