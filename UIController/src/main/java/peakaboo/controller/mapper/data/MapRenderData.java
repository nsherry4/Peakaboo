package peakaboo.controller.mapper.data;

import java.util.Map;

import peakaboo.controller.mapper.settings.OverlayChannel;
import peakaboo.controller.mapper.settings.OverlayColour;
import scitypes.Pair;
import scitypes.Spectrum;

public class MapRenderData {

	public Spectrum compositeData;
	public Map<OverlayColour, OverlayChannel> overlayData;
	public Pair<Spectrum, Spectrum> ratioData;
	
	public float maxIntensity = 0f;
	
}
