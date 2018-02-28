package peakaboo.filter.plugins.noise;

import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.IntegerStyle;
import peakaboo.calculations.Noise;
import peakaboo.filter.model.AbstractSimpleFilter;
import peakaboo.filter.model.FilterType;
import scitypes.ReadOnlySpectrum;

public class WeightedAveraging extends AbstractSimpleFilter {

	private Parameter<Integer> reach;


	public WeightedAveraging()
	{
		super();

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
		return "Weighted Averaging";
	}



	@Override
	public FilterType getFilterType()
	{

		return FilterType.NOISE;
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
				+ " filter refines the values of each point in a scan by sampling it and the points around it, and replacing it with an exponentially weighted average of the sampled points.";
	}


	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data)
	{
		data = Noise.WeightedMovingAverage(data, reach.getValue());
		return data;
	}

	
	@Override
	public boolean canFilterSubset()
	{
		return true;
	}


	@Override
	public boolean pluginEnabled() {
		return true;
	}


	@Override
	public String pluginVersion() {
		return "1.0";
	}

}
