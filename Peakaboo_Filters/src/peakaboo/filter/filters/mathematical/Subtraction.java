package peakaboo.filter.filters.mathematical;


import bolt.plugin.Plugin;
import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

@Plugin
public class Subtraction extends AbstractFilter
{

	private static final int AMOUNT = 0;
	
	public Subtraction()
	{
		super();
	}
	
	@Override
	public void initialize()
	{
		addParameter(AMOUNT, new Parameter(ValueType.REAL, "Amount to Subtract", 1.0));
	}
	
	@Override
	protected Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		return SpectrumCalculations.subtractFromList(data, getParameter(AMOUNT).realValue());
	}


	@Override
	public String getPluginDescription()
	{
		// TODO Auto-generated method stub
		return "The " + getPluginName() + " filter subtracts a constant value to all points on a spectrum.";
	}


	@Override
	public String getPluginName()
	{
		// TODO Auto-generated method stub
		return "Subtract";
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
		return null;
	}


	@Override
	public boolean validateParameters()
	{
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
