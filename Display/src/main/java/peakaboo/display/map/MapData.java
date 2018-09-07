package peakaboo.display.map;

import java.util.List;
import java.util.Map;

import peakaboo.controller.mapper.settings.OverlayChannel;
import peakaboo.controller.mapper.settings.OverlayColour;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import scitypes.Pair;
import scitypes.Spectrum;

public class MapData {

	public Spectrum compositeData;
	public Map<OverlayColour, OverlayChannel> overlayData;
	public Pair<Spectrum, Spectrum> ratioData;
	
	public float maxIntensity = 0f;
	
}
