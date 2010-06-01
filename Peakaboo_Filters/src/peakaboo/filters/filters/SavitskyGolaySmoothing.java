package peakaboo.filters.filters;


import java.util.List;

import peakaboo.calculations.Noise;
import peakaboo.drawing.plot.painters.PlotPainter;
import peakaboo.filters.AbstractFilter;
import peakaboo.filters.Parameter;
import peakaboo.filters.Parameter.ValueType;

/**
 * 
 * This class is a filter exposing the Savitsky-Golay Smoothing functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

public final class SavitskyGolaySmoothing extends AbstractFilter
{

	private final int	REACH	= 0;
	private final int	ORDER	= 1;
	private final int	IGNORE	= 2;
	private final int	MAX	= 3;


	public SavitskyGolaySmoothing()
	{

		super();
		parameters.add(REACH, new Parameter<Integer>(ValueType.INTEGER, "Reach of Polynomial (2n+1)", 7));
		parameters.add(ORDER, new Parameter<Integer>(ValueType.INTEGER, "Polynomial Order", 5));
		parameters.add(IGNORE, new Parameter<Boolean>(ValueType.BOOLEAN, "Only Smooth Weak Signal", false));
		parameters.add(MAX, new Parameter<Double>(ValueType.REAL, "Smoothing Cutoff: (counts)", 4.0));
		
		parameters.get(MAX).enabled = false;
	}
	
	@Override
	public String getFilterName()
	{

		return "Savitsky-Golay";
	}


	@Override
	public FilterType getFilterType()
	{

		return FilterType.NOISE;
	}



	@Override
	public boolean validateParameters()
	{

		int reach, order;
		
		// reach shouldn't be any larger than about 30, or else we start to distort the data more than we
		// would like
		reach = this.<Integer>getParameterValue(REACH);
		if (reach > 30 || reach < 1) return false;

		// a 0th order polynomial isn't going to be terribly useful, and this algorithm starts to get a little
		// wonky
		// when it goes over 10
		order = this.<Integer>getParameterValue(ORDER);
		if (order > 10 || order < 1) return false;

		// polynomial of order k needs at least k+1 data points in set.
		if (order >= reach * 2 + 1) return false;

		parameters.get(MAX).enabled = this.<Boolean>getParameterValue(IGNORE);
		
		return true;
	}


	@Override
	public String getFilterDescription()
	{
		return "The "
				+ getFilterName()
				+ " Filter attempts to remove noise by fitting a polynomial to each point p0 and its surrounding points p0-n..p0+n, and then taking the value of the polynomial at the p. A moving average is a special case of this filter with a polynomial of order 1.";
	}


	@Override
	public PlotPainter getPainter()
	{
		return null;
	}


	@Override
	public List<Double> filterApplyTo(List<Double> data, boolean cache)
	{
		return Noise.SavitskyGolayFilter(
			data, 
			this.<Integer>getParameterValue(ORDER), 
			this.<Integer>getParameterValue(REACH),
			0.0,
			(this.<Boolean>getParameterValue(IGNORE)) ? this.<Double>getParameterValue(MAX) : Double.MAX_VALUE
			
		);
	}

	@Override
	public boolean showFilter()
	{
		return true;
	}

}
