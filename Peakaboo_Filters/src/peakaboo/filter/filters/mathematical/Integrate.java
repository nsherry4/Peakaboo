package peakaboo.filter.filters.mathematical;


import peakaboo.calculations.Noise;
import peakaboo.filter.AbstractSimpleFilter;
import scitypes.Spectrum;


public class Integrate extends AbstractSimpleFilter
{


	@Override
	public void initialize()
	{

	}
	
	
	@Override
	protected Spectrum filterApplyTo(Spectrum data)
	{
		return Noise.integ(data);
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
	public boolean validateParameters()
	{
		// TODO Auto-generated method stub
		return true;
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



}
