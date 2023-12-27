package org.peakaboo.filter.plugins.mathematical;

import java.util.Optional;

import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.FilterContext;
import org.peakaboo.filter.model.FilterDescriptor;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.RealStyle;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;



public class AdditionMathFilter extends AbstractFilter
{

	private Parameter<Float> amount;

	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public void initialize() {
		amount = new Parameter<>("Amount to Add", new RealStyle(), 1.0f);
		addParameter(amount);
	}
	
	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, Optional<FilterContext> ctx) {
		return SpectrumCalculations.subtractFromList(data, 0.0f-amount.getValue().floatValue());
	}


	@Override
	public String getFilterDescription() {
		return "The " + getFilterName() + " filter adds a constant value to all points on a spectrum.";
	}


	@Override
	public String getFilterName() {
		return "Add";
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
	public String pluginUUID() {
		return "76b1f0cc-a825-44ec-aeac-d4b1bc38382a";
	}
	
	
}
