package org.peakaboo.filter.plugins.advanced;

import java.util.List;

import org.peakaboo.common.Version;
import org.peakaboo.curvefit.peak.search.searcher.DerivativePeakSearcher;
import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.filter.model.FilterType;
import org.peakaboo.filter.plugins.noise.LowStatisticsNoiseFilter;
import org.peakaboo.filter.plugins.noise.SavitskyGolayNoiseFilter;
import org.peakaboo.filter.plugins.noise.WeightedAverageNoiseFilter;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.framework.cyclops.SpectrumCalculations;

public class PeakDetectorFilter extends AbstractFilter {

	@Override
	public String getFilterName() {
		// TODO Auto-generated method stub
		return "Peak Finder";
	}

	@Override
	public String getFilterDescription() {
		return "Peak Finder";
	}

	@Override
	public FilterType getFilterType() {
		return FilterType.ADVANCED;
	}

	@Override
	public void initialize() {}

	@Override
	public boolean canFilterSubset() {
		return true;
	}

	@Override
	public boolean pluginEnabled() {
		return !Version.release;
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "d2a59db0-66a5-4ff0-aac6-a9b8206098ca";
	}

	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data) {
		
		List<Integer> indexes = new DerivativePeakSearcher().search(data);
		Spectrum peaks = new ISpectrum(data.size());
		for (int i : indexes) {
			peaks.set(i, 100);
		}
		return peaks;
		
	}

}
