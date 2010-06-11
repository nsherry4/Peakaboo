package peakaboo.filters.filters;


import java.util.List;

import peakaboo.calculations.Noise;
import peakaboo.datatypes.Spectrum;
import peakaboo.drawing.plot.painters.PlotPainter;
import peakaboo.filters.AbstractFilter;
import peakaboo.filters.Parameter;
import peakaboo.filters.Parameter.ValueType;

/**
 * 
 * This class is a filter exposing the Moving Average functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

public final class MovingAverage extends AbstractFilter
{

	private final int	REACH	= 0;


	public MovingAverage()
	{
		super();
		parameters.add(REACH, new Parameter<Integer>(ValueType.INTEGER, "Averaging Reach (2n+1)", 4));

	}


	@Override
	public String getFilterName()
	{
		return "Moving Average";
	}



	@Override
	public FilterType getFilterType()
	{

		return FilterType.NOISE;
	}


	@Override
	public boolean validateParameters()
	{

		int reach;

		// has to at least have a 3-point, but cannot exceed a 10*2+1=21-point moving average
		reach = this.<Integer>getParameterValue(REACH);
		if (reach > 10 || reach < 1) return false;

		return true;
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The "
				+ getFilterName()
				+ " Filter refines the values of each point in a scan by sampling it and the points around it, and replacing it with an average of the sampled points.";
	}


	@Override
	public PlotPainter getPainter()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Spectrum filterApplyTo(Spectrum data, boolean cache)
	{
		data = Noise.MovingAverage(data, this.<Integer>getParameterValue(REACH));
		return data;
	}

	@Override
	public boolean showFilter()
	{
		return true;
	}

}
