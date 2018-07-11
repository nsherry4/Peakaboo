package peakaboo.display.map;

import java.util.Map;

import scitypes.Pair;
import scitypes.Spectrum;

public class MapData {

	public Spectrum compositeData;
	public Map<OverlayColour, Spectrum> overlayData;
	public Pair<Spectrum, Spectrum> ratioData;
	
	public float maxIntensity = 0f;
	
}
