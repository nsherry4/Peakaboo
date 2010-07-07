package peakaboo.filters.filters;

import peakaboo.filters.AbstractFilter;
import peakaboo.filters.Parameter;
import peakaboo.filters.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;



public class Addition extends AbstractFilter
{

	private static final int AMOUNT = 0;
	
	public Addition()
	{

		super();
		parameters.put(AMOUNT, new Parameter<Double>(ValueType.REAL, "Amount to Add", 1.0));

	}
	
	@Override
	public Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		return SpectrumCalculations.subtractFromList(data, 0.0f-this.<Double>getParameterValue(AMOUNT).floatValue());
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The" + getFilterName() + " filter adds a constant value to all points on a spectrum.";
	}


	@Override
	public String getFilterName()
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
	public boolean showFilter()
	{
		return true;
	}
	
}
