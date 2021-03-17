package org.peakaboo.filter.plugins.mathematical;


import org.peakaboo.dataset.DataSet;
import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.FilterType;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;


public class IntegralMathFilter extends AbstractFilter
{

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public void initialize() {
		//NOOP
	}
	
	
	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, DataSet dataset) {
		return integ(data);
	}
	

	@Override
	public String getFilterDescription() {
		return "The " + getFilterName() + " transforms the data such that each channel represents the sum of itself and all channels prior to it.";
	}


	@Override
	public String getFilterName() {
		return "Integral";
	}


	@Override
	public FilterType getFilterType() {
		return FilterType.MATHEMATICAL;
	}

	
	@Override
	public boolean pluginEnabled()
	{
		return true;
	}
	
	
	@Override
	public boolean canFilterSubset() {
		return true;
	}
	
	
	
	/**
	 * Calculates the integral (sums up to X) for a spectrum
	 * @param list the data to find the integral for
	 * @return a list of sums
	 */
	public static Spectrum integ(ReadOnlySpectrum list) {
		return SpectrumCalculations.integral(list); 
	}


	@Override
	public String pluginUUID() {
		return "c49d30f9-481a-41cb-ab32-23f4207e07bb";
	}
	

}
