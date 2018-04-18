package peakaboo.filter.plugins.noise;


import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.IntegerStyle;
import peakaboo.filter.model.AbstractSimpleFilter;
import peakaboo.filter.model.FilterType;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

/**
 * 
 * This class is a filter exposing the Moving Average functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */


public final class FlatAveraging extends AbstractSimpleFilter
{

	private Parameter<Integer> reach;


	public FlatAveraging()
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
		return "Flat Averaging";
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
				+ " filter refines the values of each point in a scan by sampling it and the points around it, and replacing it with an average of the sampled points.";
	}


	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data)
	{
		data = MovingAverage(data, reach.getValue());
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

	
	
	/**
	 * 
	 * Performs a straight moving average over a data set
	 * 
	 * @param data
	 *            the data to be averaged
	 * @param windowSpan
	 *            the distance from the centrepoint to an edge of the set of numbers being averaged
	 * @return a moving-average smoothed data set
	 */
	public static Spectrum MovingAverage(ReadOnlySpectrum data, int windowSpan)
	{

		Spectrum smoothed = new ISpectrum(data.size());

		int start, stop;
		float sum;
		for (int i = 0; i < data.size(); i++) {

			// exact same as in last loop
			start = i - windowSpan;
			stop = i + windowSpan + 1;

			if (start < 0) start = 0;
			if (stop >= data.size()) stop = data.size() - 1;

			sum = SpectrumCalculations.sumValuesInList(data, start, stop);

			smoothed.set(i, sum / (stop - start));

		}


		return smoothed;

	}

	

}
