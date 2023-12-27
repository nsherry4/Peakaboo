package org.peakaboo.filter.plugins.advanced;

import java.util.List;
import java.util.Optional;

import org.peakaboo.app.Version;
import org.peakaboo.app.Version.ReleaseType;
import org.peakaboo.curvefit.peak.search.searcher.DerivativePeakSearcher;
import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.FilterDescriptor;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

public class PeakDetectorFilter extends AbstractFilter {

	@Override
	public String getFilterName() {
		return "Peak Finder";
	}

	@Override
	public String getFilterDescription() {
		return "Peak Finder plugin is a diagnostic plugin to evaluate and debug Peakaboo's peak-finding function";
	}

	@Override
	public FilterDescriptor getFilterDescriptor() {
		return FilterDescriptor.ADVANCED;
	}


	@Override
	public void initialize() {
		//NOOP
	}

	@Override
	public boolean canFilterSubset() {
		return true;
	}

	@Override
	public boolean pluginEnabled() {
		return Version.releaseType != ReleaseType.RELEASE;
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
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, Optional<FilterContext> ctx) {
		
		List<Integer> indexes = new DerivativePeakSearcher().search(data);
		Spectrum peaks = new ISpectrum(data.size());
		for (int i : indexes) {
			peaks.set(i, 100);
		}
		return peaks;
		
	}

}
