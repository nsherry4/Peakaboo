package peakaboo.filter.filters.advanced;

import peakaboo.filter.AbstractFilter;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;


public class Identity extends AbstractFilter
{

	@Override
	public boolean canFilterSubset()
	{
		return true;
	}


	@Override
	protected Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		return data;
	}


	@Override
	public String getPluginDescription()
	{
		return "This filter is the identity function -- it does no processing to the data";
	}


	@Override
	public String getPluginName()
	{
		return "None";
	}


	@Override
	public FilterType getFilterType()
	{
		return FilterType.ADVANCED;
	}


	@Override
	public PlotPainter getPainter()
	{
		return null;
	}


	@Override
	public void initialize()
	{

	}


	@Override
	public boolean pluginEnabled()
	{
		return false;
	}


	@Override
	public boolean validateParameters()
	{
		return true;
	}

}
