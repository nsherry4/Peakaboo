package peakaboo.filter.plugins.background;



import autodialog.model.Parameter;
import autodialog.view.editors.IntegerEditor;
import peakaboo.calculations.Background;
import peakaboo.filter.model.AbstractBackgroundFilter;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

/**
 * 
 * This class is a filter exposing the Parabolic Background Removal functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */


public final class LinearTrimRemoval extends AbstractBackgroundFilter
{

	private Parameter<Integer> width;
	private Parameter<Integer> iterations;


	public LinearTrimRemoval()
	{
		super();
	}

	@Override
	public void initialize()
	{
		iterations = new Parameter<>("Iterations", new IntegerEditor(), 2);
		width = new Parameter<>("Width of Fitting", new IntegerEditor(), 100);
		
		addParameter(iterations, width);
	}

	@Override
	public String getFilterName()
	{
		return "Linear Trim";
	}


	@Override
	protected Spectrum getBackground(Spectrum data, int percent)
	{

		return SpectrumCalculations.multiplyBy(
				Background.calcBackgroundLinearTrim(data, width.getValue(), iterations.getValue()), (percent/100.0f)
			);		
	}
	

	@Override
	public boolean validateCustomParameters()
	{

		// parabolas which are too wide are useless, but ones that are too
		// narrow remove good data
		if (width.getValue() > 400 || width.getValue() < 10) return false;
		if (iterations.getValue() > 20 || iterations.getValue() <= 0) return false;

		return true;
	}


	@Override
	public String getFilterDescription()
	{
		return "The "
				+ getFilterName()
				+ " filter attempts to determine which portion of the signal is background and remove it. It does this by examining all pairs of points which are n channels apart (ie (1, 10), (2, 11) where n = 10). For each pair of points, any signal which exceeds a straight line connecting the two points is removed.";
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
