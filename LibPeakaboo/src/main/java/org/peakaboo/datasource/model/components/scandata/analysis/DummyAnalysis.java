package org.peakaboo.datasource.model.components.scandata.analysis;

import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;

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
	public void process(ReadOnlySpectrum t) {}

	@Override
	public int channelsPerScan() {
		return 0;
	}

}
