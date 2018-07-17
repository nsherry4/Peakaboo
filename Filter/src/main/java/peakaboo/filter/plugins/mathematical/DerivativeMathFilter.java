package peakaboo.filter.plugins.mathematical;


import peakaboo.filter.model.AbstractSimpleFilter;
import peakaboo.filter.model.FilterType;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


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

}
