package org.peakaboo.datasource.model.components.scandata.analysis;

import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

public class DataSourceAnalysis implements Analysis {

	protected int channelCount;
	protected Spectrum summedSpectrum;
	protected int summedScanCount;
	protected Spectrum maximumSpectrum;
	protected float maxValue;

	public DataSourceAnalysis() {
		channelCount = -1;
	}
	
	public void init(int channelCount) {
		this.channelCount = channelCount;
		summedSpectrum = new ISpectrum(channelCount);
		summedScanCount = 0;
		maximumSpectrum = new ISpectrum(channelCount);
		maxValue = 0;
	}

	@Override
	public void process(ReadOnlySpectrum spectrum) {
		if (spectrum == null) { return; }
		
		// if this is the first (non-null) spectrum that we're seeing, use it to detect
		// the channel count and initialize things
		if (channelCount == -1) {
			init(spectrum.size());
		}
		
		SpectrumCalculations.addLists_inplace(summedSpectrum, spectrum);
		summedScanCount++;
		SpectrumCalculations.maxLists_inplace(maximumSpectrum, spectrum);
		maxValue = Math.max(maxValue, spectrum.max());
	}

	@Override
	public Spectrum maximumPlot() {
		return new ISpectrum(maximumSpectrum);
	}

	@Override
	public float maximumIntensity() {
		return maxValue;
	}

	@Override
	public Spectrum averagePlot() {
		return SpectrumCalculations.divideBy(summedSpectrum, summedScanCount);
	}

	@Override
	public int channelsPerScan() {
		return channelCount;
	}

}
