package peakaboo.filter.filters.background;



import bolt.plugin.Plugin;
import peakaboo.calculations.Background;
import peakaboo.filter.AbstractBackgroundFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

/**
 * 
 * This class is a filter exposing the Parabolic Background Removal functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

@Plugin
public final class BruknerRemoval extends AbstractBackgroundFilter
{

	private final int	WIDTH		= getNextParameterIndex();
	private final int	ITERATIONS 	= getNextParameterIndex();


	public BruknerRemoval()
	{
		super();

	}
	
	@Override
	public void initialize()
	{
		addParameter(WIDTH, new Parameter(ValueType.INTEGER, "Width of Fitting", 100));
		addParameter(ITERATIONS, new Parameter(ValueType.INTEGER, "Iterations", 10));
	}


	@Override
	public String getFilterName()
	{
		return "Brukner";
	}


	@Override
	protected Spectrum getBackground(Spectrum data, int percent)
	{
	
		int windowSize = getParameter(WIDTH).intValue();
		int iterations = getParameter(ITERATIONS).intValue();
		
		return SpectrumCalculations.multiplyBy(  Background.calcBackgroundBrukner(data, windowSize, iterations), (percent/100.0f));

	}


	@Override
	public boolean validateCustomParameters()
	{

		int width, iterations;

		// parabolas which are too wide are useless, but ones that are too
		// narrow remove good data
		width = getParameter(WIDTH).intValue();
		iterations = getParameter(ITERATIONS).intValue();
		
		
		if (width > 400 || width < 10) return false;
		if (iterations > 50 || iterations < 0) return false;

		return true;
	}


	@Override
	public String getFilterDescription()
	{
		return "The "
				+ getFilterName()
				+ " filter attempts to determine which portion of the signal is background and remove it. It does this over several iterations by smoothing the data and taking the minimum of the unsmoothed and smoothed data for each channel.";
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
