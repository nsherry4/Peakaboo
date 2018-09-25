package peakaboo.controller.mapper.settings;

import java.util.List;

import peakaboo.curvefit.peak.transition.TransitionSeries;
import scitypes.Spectrum;

public class OverlayChannel {
	public Spectrum data;
	public List<TransitionSeries> elements;
	
	public OverlayChannel(Spectrum data, List<TransitionSeries> elements) {
		this.data = data;
		this.elements = elements;
	}
	
}