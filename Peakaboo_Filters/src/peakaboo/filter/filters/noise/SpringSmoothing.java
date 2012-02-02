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
public final class SpringSmoothing extends AbstractSimpleFilter
{

	private final int	MULTIPLIER		= 0;
	private final int	ITERATIONS		= 1;
	private final int	FALLOFF			= 2;


	public SpringSmoothing()
	{
		super();
	}
	
	@Override
	public void initialize()
	{
		addParameter(FALLOFF, new Parameter(ValueType.REAL, "Exponential Force Falloff Rate", 2.0d));
		addParameter(MULTIPLIER, new Parameter(ValueType.REAL, "Linear Force Multiplier", 20.0d));
		addParameter(ITERATIONS, new Parameter(ValueType.INTEGER, "Iterations", 20));
	}


	@Override
	public String getFilterName()
	{
		return "Spring Smoothing";
	}



	@Override
	public FilterType getFilterType()
	{

		return FilterType.NOISE;
	}


	@Override
	public boolean validateParameters()
	{

		float mult, power;
		int iterations;

		
		mult = getParameter(MULTIPLIER).realValue();
		if (mult > 100 || mult < 0.1) return false;
		
		power = getParameter(FALLOFF).realValue();
		if (power > 10 || power <= 0.0) return false;
		
		iterations = getParameter(ITERATIONS).intValue();
		if (iterations > 50 || iterations < 1) return false;
		

		return true;
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The "
				+ getFilterName()
				+ " filter operates on the assumption that weak signal should be smoothed more than strong signal. It treats each pair of points as if they were connected by a spring. With each iteration, a tension force draws neighbouring points closer together. The Force Multiplier controls how strongly a pair of elements are pulled together, and the Force Falloff Rate controls how aggressively stronger signal is anchored in place, unmoved by spring forces. This prevents peak shapes from being distorted by the smoothing algorithm.";
	}


	@Override
	public Spectrum filterApplyTo(Spectrum data)
	{
		data = Noise.SpringFilter(
				data, 
				getParameter(MULTIPLIER).realValue(), 
				getParameter(FALLOFF).realValue(), 
				getParameter(ITERATIONS).intValue()
			);
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
