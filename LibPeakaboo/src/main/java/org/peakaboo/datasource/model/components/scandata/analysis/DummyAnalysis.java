package org.peakaboo.datasource.model.components.scandata.analysis;

import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.Spectrum;

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
