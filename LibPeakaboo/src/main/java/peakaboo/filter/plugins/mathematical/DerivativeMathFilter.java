package peakaboo.filter.plugins.mathematical;


import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import cyclops.SpectrumCalculations;
import peakaboo.filter.model.AbstractSimpleFilter;
import peakaboo.filter.model.FilterType;


public class DerivativeMathFilter extends AbstractSimpleFilter
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
		return deriv(data);
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The " + getFilterName() + " transforms the data such that each channel represents the difference between itself and the channel before it.";
	}


	@Override
	public String getFilterName()
	{
		// TODO Auto-generated method stub
		return "Derivative";
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
	 * Calculates the derivitive (deltas) for a spectrum
	 * @param list the data to find the deltas for
	 * @return a list of deltas
	 */
	public static Spectrum deriv(ReadOnlySpectrum list)
	{
	
		return SpectrumCalculations.derivative(list);
		
	}

	@Override
	public String pluginUUID() {
		return "779ca35d-0f68-4ea9-b3f4-4aef46977477";
	}
	
}
