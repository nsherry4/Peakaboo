package peakaboo.filter.filters.mathematical;

import peakaboo.filter.AbstractFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;



public class Addition extends AbstractFilter
{

	private static final int AMOUNT = 0;
	
	public Addition()
	{
		super();
	}
	
	@Override
	public void initialize()
	{
		addParameter(AMOUNT, new Parameter(ValueType.REAL, "Amount to Add", 1.0));
	}
	
	@Override
	protected Spectrum filterApplyTo(Spectrum data, boolean cache)
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
