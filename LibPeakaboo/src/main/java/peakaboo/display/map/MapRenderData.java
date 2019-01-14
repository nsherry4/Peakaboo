package peakaboo.display.map;

import java.util.Map;

import cyclops.Pair;
import cyclops.Spectrum;
import peakaboo.display.map.modes.OverlayChannel;
import peakaboo.display.map.modes.OverlayColour;

public class MapRenderData {

	public Spectrum compositeData;
	public Map<OverlayColour, OverlayChannel> overlayData;
	public Pair<Spectrum, Spectrum> ratioData;
	
	public float maxIntensity = 0f;
	
}
