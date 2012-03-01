package peakaboo.filter.filters.mathematical;


import bolt.plugin.Plugin;
import peakaboo.calculations.Noise;
import peakaboo.filter.AbstractSimpleFilter;
import scitypes.Spectrum;

@Plugin
public class Derivative extends AbstractSimpleFilter
{

	
	@Override
	public void initialize()
	{

	}
	
	@Override
	protected Spectrum filterApplyTo(Spectrum data)
	{
		return Noise.deriv(data);
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

	@Override
	public boolean showSaveLoad()
	{
		return false;
	}
	
}
