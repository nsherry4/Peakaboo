package org.peakaboo.dataset.source.model.components.scandata.analysis;

import java.util.List;

import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

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
		summedSpectrum = new ArraySpectrum(channelCount);
		summedScanCount = 0;
		maximumSpectrum = new ArraySpectrum(channelCount);
		maxValue = 0;
	}

	@Override
	public void process(SpectrumView spectrum) {
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
	public SpectrumView maximumPlot() {
		return new ArraySpectrum(maximumSpectrum);
	}

	@Override
	public float maximumIntensity() {
		return maxValue;
	}

	@Override
	public SpectrumView averagePlot() {
		return SpectrumCalculations.divideBy(summedSpectrum, summedScanCount);
	}

	@Override
	public int channelsPerScan() {
		return channelCount;
	}

	@Override
	public SpectrumView summedPlot() {
		return new ArraySpectrum(summedSpectrum);
	}

	@Override
	public int scanCount() {
		return summedScanCount;
	}


	public static DataSourceAnalysis merge(List<Analysis> analyses) {
		DataSourceAnalysis result = new DataSourceAnalysis();
		result.init(analyses.get(0).channelsPerScan());
		for (var analysis : analyses) {
			SpectrumCalculations.addLists_inplace(result.summedSpectrum, analysis.summedPlot());
			SpectrumCalculations.maxLists_inplace(result.maximumSpectrum, analysis.maximumPlot());
			result.summedScanCount += analysis.scanCount();
			result.maxValue = Math.max(result.maxValue, analysis.maximumIntensity());
		}
		return result;
	}

}
