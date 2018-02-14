package peakaboo.filter.plugins.background;



import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.IntegerStyle;
import peakaboo.calculations.Background;
import peakaboo.filter.model.AbstractBackgroundFilter;
import scitypes.ReadOnlySpectrum;
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
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public void initialize()
	{
		width = new Parameter<>("Width of Fitting", new IntegerStyle(), 100, this::validate);
		iterations = new Parameter<>("Iterations", new IntegerStyle(), 10, this::validate);
		
		addParameter(width, iterations);
	}


	@Override
	public String getFilterName()
	{
		return "Brukner";
	}


	@Override
	protected ReadOnlySpectrum getBackground(ReadOnlySpectrum data, int percent)
	{		
		return SpectrumCalculations.multiplyBy(
				Background.calcBackgroundBrukner(data, width.getValue(), iterations.getValue()), (percent/100.0f)
			);
	}


	private boolean validate(Parameter<?> p)
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
