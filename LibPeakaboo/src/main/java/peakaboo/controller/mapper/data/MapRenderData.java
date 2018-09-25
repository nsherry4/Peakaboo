package peakaboo.controller.mapper.data;

import java.util.Map;

import cyclops.Pair;
import cyclops.Spectrum;
import peakaboo.controller.mapper.settings.OverlayChannel;
import peakaboo.controller.mapper.settings.OverlayColour;

public class MapRenderData {

	public Spectrum compositeData;
	public Map<OverlayColour, OverlayChannel> overlayData;
	public Pair<Spectrum, Spectrum> ratioData;
	
	public float maxIntensity = 0f;
	
}
