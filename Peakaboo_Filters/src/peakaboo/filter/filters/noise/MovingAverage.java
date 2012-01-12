package peakaboo.filter.filters.noise;


import bolt.plugin.Plugin;
import peakaboo.calculations.Noise;
import peakaboo.filter.AbstractSimpleFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scitypes.Spectrum;

/**
 * 
 * This class is a filter exposing the Moving Average functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

@Plugin
public final class MovingAverage extends AbstractSimpleFilter
{

	private final int	REACH	= 0;


	public MovingAverage()
	{
		super();

	}

	@Override
	public void initialize()
	{
		addParameter(REACH, new Parameter(ValueType.INTEGER, "Averaging Reach (2n+1)", 4));
	}
	
	@Override
	public String getPluginName()
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
		reach = getParameter(REACH).intValue();
		if (reach > 10 || reach < 1) return false;

		return true;
	}


	@Override
	public String getPluginDescription()
	{
		// TODO Auto-generated method stub
		return "The "
				+ getPluginName()
				+ " filter refines the values of each point in a scan by sampling it and the points around it, and replacing it with an average of the sampled points.";
	}


	@Override
	protected Spectrum filterApplyTo(Spectrum data)
	{
		data = Noise.MovingAverage(data, getParameter(REACH).intValue());
		return data;
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
