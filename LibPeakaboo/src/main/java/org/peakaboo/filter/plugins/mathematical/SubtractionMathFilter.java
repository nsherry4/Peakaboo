package org.peakaboo.filter.plugins.mathematical;


import java.util.Optional;

import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.FilterDescriptor;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.RealStyle;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;


public class SubtractionMathFilter extends AbstractFilter
{

	private Parameter<Float> amount;
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public void initialize() {
		amount = new Parameter<>("Amount to Subtract", new RealStyle(), 1.0f);
		addParameter(amount);
	}
	
	@Override
	protected SpectrumView filterApplyTo(SpectrumView data, Optional<FilterContext> ctx) {
		return SpectrumCalculations.subtractFromList(data, amount.getValue().floatValue());
	}


	@Override
	public String getFilterDescription() {
		return "The " + getFilterName() + " filter subtracts a constant value to all points on a spectrum.";
	}


	@Override
	public String getFilterName() {
		return "Subtract";
	}


	@Override
	public FilterDescriptor getFilterDescriptor() {
		return FilterDescriptor.MATHEMATICAL;
	}

	@Override
	public boolean canFilterSubset() {
		return true;
	}

	
	@Override
	public String getFilterUUID() {
		return "06557ce2-5587-4e73-abdb-f2d5dbb16f81";
	}
	
	
}
