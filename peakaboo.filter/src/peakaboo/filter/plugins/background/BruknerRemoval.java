package peakaboo.filter.plugins.background;



import autodialog.model.Parameter;
import autodialog.view.editors.IntegerEditor;
import peakaboo.calculations.Background;
import peakaboo.filter.plugins.AbstractBackgroundFilter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

/**
 * 
 * This class is a filter exposing the Parabolic Background Removal functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

public final class BruknerRemoval extends AbstractBackgroundFilter
{

	private Parameter<Integer> width;
	private Parameter<Integer> iterations;


	public BruknerRemoval()
	{
		super();

	}
	
	@Override
	public void initialize()
	{
		width = new Parameter<>("Width of Fitting", new IntegerEditor(), 100);
		iterations = new Parameter<>("Iterations", new IntegerEditor(), 10);
		
		addParameter(width, iterations);
	}


	@Override
	public String getFilterName()
	{
		return "Brukner";
	}


	@Override
	protected Spectrum getBackground(Spectrum data, int percent)
	{		
		return SpectrumCalculations.multiplyBy(
				Background.calcBackgroundBrukner(data, width.getValue(), iterations.getValue()), (percent/100.0f)
			);
	}


	@Override
	public boolean validateCustomParameters()
	{


		// parabolas which are too wide are useless, but ones that are too
		// narrow remove good data
		if (width.getValue() > 400 || width.getValue() < 10) return false;
		if (iterations.getValue() > 50 || iterations.getValue() < 0) return false;

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
