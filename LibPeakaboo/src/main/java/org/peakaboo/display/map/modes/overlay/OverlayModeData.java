package org.peakaboo.display.map.modes.overlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.peakaboo.display.map.modes.MapModeData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.SigDigits;

public class OverlayModeData implements MapModeData {

	private Coord<Integer> size;
	private Map<OverlayColour, OverlayChannel> data;
	private boolean relative;
	
	public OverlayModeData(Map<OverlayColour, OverlayChannel> data, Coord<Integer> size, boolean relative) {
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
		if (!isValidPoint(coord)) {
			return "-";
		}
		
		if (this.relative) return "--";
		
		int index = getIndex(coord);
		List<String> results = new ArrayList<String>();
		for (OverlayColour c : OverlayColour.values()) {
			if (data.get(c) != null && data.get(c).data != null) {
				results.add(  c.toString() + ": " + SigDigits.roundFloatTo(data.get(c).data.get(index), 2)  );
			}
		}
		return results.stream().collect(Collectors.joining(", "));
	}

	public Map<OverlayColour, OverlayChannel> getData() {
		return data;
	}

}
