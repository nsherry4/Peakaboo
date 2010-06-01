package peakaboo.filters.filters;

import java.util.List;

import peakaboo.calculations.ListCalculations;
import peakaboo.common.Version;
import peakaboo.drawing.plot.painters.PlotPainter;
import peakaboo.filters.AbstractFilter;
import peakaboo.filters.Parameter;
import peakaboo.filters.Parameter.ValueType;


public class Multiply extends AbstractFilter
{

	private static final int AMOUNT = 0;
	
	public Multiply()
	{

		super();
		parameters.add(AMOUNT, new Parameter<Double>(ValueType.REAL, "Multiply By", 1.0));

	}
	
	@Override
	public List<Double> filterApplyTo(List<Double> data, boolean cache)
	{
		return ListCalculations.multiplyBy(data, this.<Double>getParameterValue(AMOUNT));
	}


	@Override
	public String getFilterDescription()
	{
		return "The" + getFilterName() + " filter multiplies all points on a spectrum by a constant value.";
	}


	@Override
	public String getFilterName()
	{
		// TODO Auto-generated method stub
		return "Multiply";
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
		return !Version.release;
	}
	
}
