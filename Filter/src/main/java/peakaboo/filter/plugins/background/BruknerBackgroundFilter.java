package peakaboo.filter.plugins.background;



import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.IntegerStyle;
import peakaboo.filter.model.AbstractBackgroundFilter;
import scitypes.ISpectrum;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

/**
 * 
 * This class is a filter exposing the Parabolic Background Removal functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */

public final class BruknerBackgroundFilter extends AbstractBackgroundFilter
{

	private Parameter<Integer> width;
	private Parameter<Integer> iterations;


	public BruknerBackgroundFilter()
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
				calcBackgroundBrukner(data, width.getValue(), iterations.getValue()), (percent/100.0f)
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

	
	

	
	/**
	 * Calculates the background using the Brukner technique. Brukner technique works by taking 
	 * min(data, moving_average(data)) repeatedly. This prevents strong signal from
	 * bleeding into nearby areas, while reducing the strong signal at the same time.
	 * 
	 * @param data the {@link Spectrum} data to calculate the background from
	 * @param windowSize the window size for the moving average 
	 * @param repetitions the number of iterations of the smoothing/minvalue step to perform
	 * @return the calculated background
	 */
	public static Spectrum calcBackgroundBrukner(ReadOnlySpectrum data, int windowSize, int repetitions)
	{

		// FIRST STEP
		float Iavg = data.sum() / data.size();
		float Imin = data.min();
		float diff = Iavg - Imin;
		final float cutoff = Iavg + 2 * diff;

		Spectrum result = new ISpectrum(data); 

		//initially cap the data at the given cutoff
		for (int i = 0; i < result.size(); i++)
		{
			if (result.get(i) > cutoff) result.set(i, cutoff);
		}
		
		Spectrum result2 = new ISpectrum(result.size());

		int i = 0;
		while (repetitions > 0)
		{
			removeBackgroundBruknerIteration(result, result2, windowSize);
			
			i++;
			if (i > repetitions)
			{
				result = result2;
				break;
			}

			removeBackgroundBruknerIteration(result2, result, windowSize);
			
			i++;
			if (i > repetitions) break;

		}

		return result;

	}
	
	/**
	 * Performs a single iteration of the brukner min(data, moving average) process
	 * @param source the data to look at
	 * @param target the {@link Spectrum} to write the new values out to
	 * @param windowSize the window size for the moving average
	 */
	private static void removeBackgroundBruknerIteration(final Spectrum source, final Spectrum target, final int windowSize)
	{

		for (int i = 0; i < source.size(); i++)
		{
			int start, stop;
			start = Math.max(i - windowSize, 0);
			stop = Math.min(i + windowSize+1, source.size() - 1);
			float average = SpectrumCalculations.sumValuesInList(source, start, stop) / (windowSize * 2 + 1);
			target.set(i, Math.min(average, source.get(i)));
			
		}
		
	}
	
	
	@Override
	public String pluginUUID() {
		return "03e7a19d-d0bc-4508-9a2d-6a887d2e74bb";
	}


}
