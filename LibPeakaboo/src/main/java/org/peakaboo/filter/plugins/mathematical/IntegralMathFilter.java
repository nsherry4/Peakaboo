package org.peakaboo.filter.plugins.mathematical;


import org.peakaboo.filter.model.AbstractSimpleFilter;
import org.peakaboo.filter.model.FilterType;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.framework.cyclops.SpectrumCalculations;


public class IntegralMathFilter extends AbstractSimpleFilter
{

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public void initialize()
	{

	}
	
	
	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data)
	{
		return integ(data);
	}
	

	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The " + getFilterName() + " transforms the data such that each channel represents the sum of itself and all channels prior to it.";
	}


	@Override
	public String getFilterName()
	{
		// TODO Auto-generated method stub
		return "Integral";
	}


	@Override
	public FilterType getFilterType()
	{
		// TODO Auto-generated method stub
		return FilterType.MATHEMATICAL;
	}

	
	@Override
	public boolean pluginEnabled()
	{
		return true;
	}
	
	
	@Override
	public boolean canFilterSubset()
	{
		return true;
	}
	
	
	
	/**
	 * Calculates the integral (sums up to X) for a spectrum
	 * @param list the data to find the integral for
	 * @return a list of sums
	 */
	public static Spectrum integ(ReadOnlySpectrum list)
	{
		
		return SpectrumCalculations.integral(list); 
		
	}


	@Override
	public String pluginUUID() {
		return "c49d30f9-481a-41cb-ab32-23f4207e07bb";
	}
	

}
