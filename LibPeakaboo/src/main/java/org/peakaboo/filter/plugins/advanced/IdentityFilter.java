package org.peakaboo.filter.plugins.advanced;

import java.util.Optional;

import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.FilterDescriptor;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;

public class IdentityFilter extends AbstractFilter {

	@Override
	public boolean canFilterSubset() {
		return true;
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, Optional<FilterContext> ctx) {
		return data;
	}

	@Override
	public String getFilterDescription() {
		return "This filter is the identity function -- it does no processing to the data";
	}

	@Override
	public String getFilterName() {
		return "None";
	}

	@Override
	public FilterDescriptor getFilterDescriptor() {
		return FilterDescriptor.ADVANCED;
	}

	@Override
	public void initialize() {
		// NOOP
	}

	@Override
	public boolean pluginEnabled() {
		return false;
	}

	@Override
	public String getFilterUUID() {
		return "da022b2e-08e1-479c-ac3a-91ab3e1dd116";
	}

}
