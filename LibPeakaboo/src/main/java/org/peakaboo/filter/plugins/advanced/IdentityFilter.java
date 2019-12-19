package org.peakaboo.filter.plugins.advanced;

import org.peakaboo.dataset.DataSet;
import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.FilterType;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;

public class IdentityFilter extends AbstractFilter
{

	@Override
	public boolean canFilterSubset() {
		return true;
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, DataSet dataset) {
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
	public FilterType getFilterType() {
		return FilterType.ADVANCED;
	}


	@Override
	public void initialize() {
		//NOOP
	}


	@Override
	public boolean pluginEnabled() {
		return false;
	}
	
	@Override
	public String pluginUUID() {
		return "da022b2e-08e1-479c-ac3a-91ab3e1dd116";
	}


}
