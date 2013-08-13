package peakaboo.filter.filters.background;


import autodialog.model.Parameter;
import autodialog.view.editors.IntegerEditor;
import peakaboo.calculations.Background;
import peakaboo.filter.AbstractBackgroundFilter;
import scitypes.Spectrum;

/**
 * 
 * This class is a filter exposing the Parabolic Background Removal functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */


public final class PolynomialRemoval extends AbstractBackgroundFilter
{

	private Parameter<Integer> width;
	private Parameter<Integer> power;


	public PolynomialRemoval()
	{
		super();
	}
	
	@Override
	public void initialize()
	{
		width = new Parameter<>("Width of Polynomial", new IntegerEditor(), 300);
		power = new Parameter<>("Power of Polynomial", new IntegerEditor(), 3);
		
		addParameter(width, power);
	}


	@Override
	public String getFilterName()
	{
		return "Polynomial";
	}


	@Override
	protected Spectrum getBackground(Spectrum data, int percent)
	{
		return Background.calcBackgroundParabolic(data, width.getValue(), power.getValue(), percent / 100.0f);
	}



	@Override
	public boolean validateCustomParameters()
	{
		// parabolas which are too wide are useless, but ones that are too
		// narrow remove good data
		
		if (width.getValue() > 800 || width.getValue() < 50) return false;
		if (power.getValue() > 128 || power.getValue() < 0) return false;

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
