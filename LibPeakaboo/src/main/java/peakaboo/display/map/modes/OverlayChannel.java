package peakaboo.display.map.modes;

import java.util.List;

import cyclops.Spectrum;
import peakaboo.curvefit.peak.transition.ITransitionSeries;
import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;

public class OverlayChannel {
	public Spectrum data;
	public List<ITransitionSeries> elements;
	
	public OverlayChannel(Spectrum data, List<ITransitionSeries> elements) {
		this.data = data;
		this.elements = elements;
	}
	
}