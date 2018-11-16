package peakaboo.display.map.modes;

import java.util.List;

import cyclops.Spectrum;
import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;

public class OverlayChannel {
	public Spectrum data;
	public List<LegacyTransitionSeries> elements;
	
	public OverlayChannel(Spectrum data, List<LegacyTransitionSeries> elements) {
		this.data = data;
		this.elements = elements;
	}
	
}