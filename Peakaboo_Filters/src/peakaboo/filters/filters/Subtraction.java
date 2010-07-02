package peakaboo.filters.filters;


import peakaboo.filters.AbstractFilter;
import peakaboo.filters.Parameter;
import peakaboo.filters.Parameter.ValueType;
import scidraw.drawing.plot.painters.PlotPainter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


public class Subtraction extends AbstractFilter
{

	private static final int AMOUNT = 0;
	
	public Subtraction()
	{

		super();
		parameters.add(AMOUNT, new Parameter<Double>(ValueType.REAL, "Amount to Subtract", 1.0));

	}
	
	@Override
	public Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		return SpectrumCalculations.subtractFromList(data, this.<Double>getParameterValue(AMOUNT).floatValue());
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The" + getFilterName() + " filter subtracts a constant value to all points on a spectrum.";
	}


	@Override
	public String getFilterName()
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
	public boolean showFilter()
	{
		return true;
	}
	
}
