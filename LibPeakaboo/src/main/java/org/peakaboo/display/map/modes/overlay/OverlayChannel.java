package org.peakaboo.display.map.modes.overlay;

import java.util.List;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.Spectrum;

public class OverlayChannel {
	public Spectrum data;
	public List<ITransitionSeries> elements;
	
	public OverlayChannel(Spectrum data, List<ITransitionSeries> elements) {
		this.data = data;
		this.elements = elements;
	}
	
}