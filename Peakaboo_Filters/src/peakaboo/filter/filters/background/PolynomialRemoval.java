package peakaboo.filter.filters.background;


import bolt.plugin.Plugin;
import peakaboo.calculations.Background;
import peakaboo.filter.AbstractBackgroundFilter;
import peakaboo.filter.Parameter;
import peakaboo.filter.Parameter.ValueType;
import scitypes.Spectrum;

/**
 * 
 * This class is a filter exposing the Parabolic Background Removal functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

@Plugin
public final class PolynomialRemoval extends AbstractBackgroundFilter
{

	private final int	WIDTH	= getNextParameterIndex();
	private final int	POWER	= getNextParameterIndex();


	public PolynomialRemoval()
	{
		super();
	}
	
	@Override
	public void initialize()
	{
		addParameter(WIDTH, new Parameter(ValueType.INTEGER, "Width of Polynomial", 300));
		addParameter(POWER, new Parameter(ValueType.INTEGER, "Power of Polynomial", 3));
	}


	@Override
	public String getFilterName()
	{
		return "Polynomial";
	}


	@Override
	protected Spectrum getBackground(Spectrum data, int percent)
	{
		return Background.calcBackgroundParabolic(
				data,
				getParameter(WIDTH).intValue(),
				getParameter(POWER).intValue(),
				percent / 100.0f);
	}



	@Override
	public boolean validateCustomParameters()
	{

		int width, power;

		// parabolas which are too wide are useless, but ones that are too
		// narrow remove good data
		width = getParameter(WIDTH).intValue();
		power = getParameter(POWER).intValue();
		
		if (width > 800 || width < 50) return false;
		if (power > 128 || power < 0) return false;

		return true;
	}


	@Override
	public String getFilterDescription()
	{
		return "The "
				+ getFilterName()
				+ " filter attempts to determine which portion of the signal is background and remove it. It accomplishes this by attempting to fit a series of parabolic (or higher order single-term) curves under the data, with a curve centred at each channel, and attempting to make each curve as tall as possible while still staying completely under the spectrum. The union of these curves is calculated and subtracted from the original data.";
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
