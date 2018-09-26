package peakaboo.display.map.modes;

import java.util.List;

import cyclops.Spectrum;
import peakaboo.curvefit.peak.transition.TransitionSeries;

public class OverlayChannel {
	public Spectrum data;
	public List<TransitionSeries> elements;
	
	public OverlayChannel(Spectrum data, List<TransitionSeries> elements) {
		this.data = data;
		this.elements = elements;
	}
	
}