package peakaboo.filter.plugins.mathematical;


import peakaboo.filter.model.AbstractSimpleFilter;
import peakaboo.filter.model.FilterType;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;


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
		
		Spectrum result = new ISpectrum(list.size());
		float val = 0;
		
		
		for (int i = 0; i < list.size(); i++)
		{
			val += list.get(i);
			result.set(i,  val );
		}
		
		return result;
		
	}



}
