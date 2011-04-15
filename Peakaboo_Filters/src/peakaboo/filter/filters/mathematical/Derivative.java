package peakaboo.filter.filters.mathematical;


import peakaboo.calculations.Noise;
import peakaboo.filter.AbstractFilter;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;


public class Derivative extends AbstractFilter
{

	
	@Override
	public void initialize()
	{

	}
	
	@Override
	protected Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		return Noise.deriv(data);
	}


	@Override
	public String getPluginDescription()
	{
		// TODO Auto-generated method stub
		return "The " + getPluginName() + " transforms the data such that each channel represents the difference between itself and the channel before it.";
	}


	@Override
	public String getPluginName()
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
	public PlotPainter getPainter()
	{
		// TODO Auto-generated method stub
		return null;
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
