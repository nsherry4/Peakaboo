package org.peakaboo.datasource.model.components.scandata.analysis;

import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public class DummyAnalysis implements Analysis {

	@Override
	public Spectrum averagePlot() {
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
	public void process(ReadOnlySpectrum t) {
		//NOOP
	}

	@Override
	public int channelsPerScan() {
		return 0;
	}

}
