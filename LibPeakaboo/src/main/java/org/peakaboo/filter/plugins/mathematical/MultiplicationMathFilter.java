package org.peakaboo.filter.plugins.mathematical;



import java.util.Optional;

import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.FilterDescriptor;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.RealStyle;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;


public class MultiplicationMathFilter extends AbstractFilter {

	private Parameter<Float> amount;
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public void initialize() {
		amount = new Parameter<>("Multiply By", new RealStyle(), 1.0f);
		addParameter(amount);
		
	}
	
	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, Optional<FilterContext> ctx) {
		return SpectrumCalculations.multiplyBy(data, amount.getValue().floatValue());
	}


	@Override
	public String getFilterDescription() {
		return "The " + getFilterName() + " filter multiplies all points on a spectrum by a constant value.";
	}


	@Override
	public String getFilterName() {
		return "Multiply";
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
		return "014cd405-0f41-4a24-9b66-10381cdf5a8c";
	}
	
	
}
