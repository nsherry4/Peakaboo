package org.peakaboo.display.map;

import java.util.Map;

import org.peakaboo.display.map.modes.correlation.CorrelationMapMode.CorrelationMapData;
import org.peakaboo.display.map.modes.overlay.OverlayChannel;
import org.peakaboo.display.map.modes.overlay.OverlayColour;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.Spectrum;

public class MapRenderData {

	public Spectrum compositeData;
	public Map<OverlayColour, OverlayChannel> overlayData;
	public Pair<Spectrum, Spectrum> ratioData;
	public CorrelationMapData correlationData;
	
	public float maxIntensity = 0f;
	
}
