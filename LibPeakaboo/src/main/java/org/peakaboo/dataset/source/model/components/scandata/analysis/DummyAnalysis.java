package org.peakaboo.dataset.source.model.components.scandata.analysis;

import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public class DummyAnalysis implements Analysis {

	@Override
	public Spectrum averagePlot() {
		return null;
	}

	@Override
	public Spectrum summedPlot() {
		return null;
	}
	
	@Override
	public float maximumIntensity() {
		return 0;
	}

	@Override
	public Spectrum maximumPlot() {
		return null;
	}

	@Override
	public void process(SpectrumView t) {
		//NOOP
	}

	@Override
	public int channelsPerScan() {
		return 0;
	}

	@Override
	public int scanCount() {
		return 0;
	}

}
