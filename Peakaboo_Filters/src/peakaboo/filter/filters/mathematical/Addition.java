package peakaboo.filter.filters.mathematical;

import bolt.plugin.Plugin;
import peakaboo.filter.AbstractSimpleFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


@Plugin
public class Addition extends AbstractSimpleFilter
{

	private static final int AMOUNT = 0;

	@Override
	public void initialize()
	{
		addParameter(AMOUNT, new Parameter(ValueType.REAL, "Amount to Add", 1.0));
	}
	
	@Override
	protected Spectrum filterApplyTo(Spectrum data)
	{
		return SpectrumCalculations.subtractFromList(data, 0.0f-getParameter(AMOUNT).realValue());
	}


	@Override
	public String getPluginDescription()
	{
		// TODO Auto-generated method stub
		return "The " + getPluginName() + " filter adds a constant value to all points on a spectrum.";
	}


	@Override
	public String getPluginName()
	{
		// TODO Auto-generated method stub
		return "Add";
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
