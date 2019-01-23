package org.peakaboo.display.map;

import java.util.Map;

import org.peakaboo.display.map.modes.OverlayChannel;
import org.peakaboo.display.map.modes.OverlayColour;

import cyclops.Pair;
import cyclops.Spectrum;

public class MapRenderData {

	public Spectrum compositeData;
	public Map<OverlayColour, OverlayChannel> overlayData;
	public Pair<Spectrum, Spectrum> ratioData;
	
	public float maxIntensity = 0f;
	
}