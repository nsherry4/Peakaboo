package org.peakaboo.datasource.model.components.scandata.analysis;

import java.util.List;

import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;

public class CombinedAnalysis implements Analysis {

	protected int channelCount;
	protected Spectrum summedSpectrum;
	protected int summedScanCount;
	protected Spectrum maximumSpectrum;
	protected float maxValue;
	
	public CombinedAnalysis(List<Analysis> analyses) {
		init(analyses.get(0).channelsPerScan());
		for (var analysis : analyses) {
			SpectrumCalculations.addLists_inplace(summedSpectrum, analysis.summedPlot());
			SpectrumCalculations.maxLists_inplace(maximumSpectrum, analysis.maximumPlot());
			summedScanCount += analysis.scanCount();
			maxValue = Math.max(maxValue, analysis.maximumIntensity());
		}
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
		throw new UnsupportedOperationException("This implementation can only report data");
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

	@Override
	public ReadOnlySpectrum summedPlot() {
		return new ISpectrum(summedSpectrum);
	}

	@Override
	public int scanCount() {
		return summedScanCount;
	}

}
