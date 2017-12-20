package peakaboo.filter.plugins.noise;


import autodialog.model.Parameter;
import autodialog.model.style.editors.IntegerStyle;
import peakaboo.calculations.Noise;
import peakaboo.filter.model.AbstractSimpleFilter;
import peakaboo.filter.model.Filter;
import scitypes.ReadOnlySpectrum;

/**
 * 
 * This class is a filter exposing the Moving Average functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */


public final class MovingAverage extends AbstractSimpleFilter
{

	private Parameter<Integer> reach;


	public MovingAverage()
	{
		super();

	}
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public void initialize()
	{
		reach = new Parameter<>("Averaging Reach (2n+1)", new IntegerStyle(), 4, this::validate);
		addParameter(reach);
	}
	
	@Override
	public String getFilterName()
	{
		return "Moving Average";
	}



	@Override
	public Filter.FilterType getFilterType()
	{

		return Filter.FilterType.NOISE;
	}


	private boolean validate(Parameter<?> p)
	{
		// has to at least have a 3-point, but cannot exceed a 10*2+1=21-point moving average
		if (reach.getValue() > 10 || reach.getValue() < 1) return false;

		return true;
	}


	@Override
	public String getFilterDescription()
	{
		// TODO Auto-generated method stub
		return "The "
				+ getFilterName()
				+ " filter refines the values of each point in a scan by sampling it and the points around it, and replacing it with an average of the sampled points.";
	}


	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data)
	{
		data = Noise.MovingAverage(data, reach.getValue());
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
