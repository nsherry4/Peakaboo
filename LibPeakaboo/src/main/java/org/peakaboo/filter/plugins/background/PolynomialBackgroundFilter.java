package org.peakaboo.filter.plugins.background;


import org.peakaboo.dataset.DataSet;
import org.peakaboo.filter.model.AbstractBackgroundFilter;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerStyle;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.framework.cyclops.SpectrumCalculations;

/**
 * 
 * This class is a filter exposing the Parabolic Background Removal functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */


public final class PolynomialBackgroundFilter extends AbstractBackgroundFilter
{

	private Parameter<Integer> width;
	private Parameter<Integer> power;


	public PolynomialBackgroundFilter()
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
		width = new Parameter<>("Width of Polynomial", new IntegerStyle(), 300, this::validate);
		power = new Parameter<>("Power of Polynomial", new IntegerStyle(), 3, this::validate);
		
		addParameter(width, power);
	}


	@Override
	public String getFilterName()
	{
		return "Polynomial";
	}


	@Override
	protected ReadOnlySpectrum getBackground(ReadOnlySpectrum data, DataSet dataset, int percent)
	{
		return calcBackgroundParabolic(data, width.getValue(), power.getValue(), percent / 100.0f);
	}



	private boolean validate(Parameter<?> p)
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

	
	

	/**
	 * Fits a parabolic curve to the underside of the data for each data point, and returns the 
	 * union of the parabolas. This is a convenience method for
	 * {@link #calcBackgroundFunctionFit(Spectrum, Spectrum, percent)}.
	 * 
	 * @param data
	 *            the data to perform subtraction on
	 * @param width
	 *            the width of the polynomial
	 * @param power
	 *            the power/order of the polynomial
	 * @param percentToRemove
	 *            0.0 - 1.0: the percent of the background which this algorithm should try to remove
	 * @return a background-subtracted list of values
	 */
	public static Spectrum calcBackgroundParabolic(ReadOnlySpectrum data, int width, int power, float percentToRemove)
	{

		// y = -(x * s)^power + m upside down parabola horizontally stretched by s and shifted upwards by m

		double centre = width / 2.0;
		double reach = 1.0 / centre;
		float value;
		double x;

		int raise = 1;
		if (power == 0) raise = 2;

		Spectrum function = new ISpectrum(width);
		for (int i = 0; i < width; i++)
		{
			x = i - centre;
			value = (float) -Math.abs(Math.pow((x * reach), power)) + raise;
			function.set(i, value);
		}

		SpectrumCalculations.normalize_inplace(function);

		return calcBackgroundFunctionFit(data, function, percentToRemove);

	}

	/**
	 * Fits a given function to the underside of the data for each data point, and returns the union of the fitted functions
	 * 
	 * @param data
	 *            the data to perform subtraction on
	 * @param function
	 *            the function (as a list of discreet values) to fit with
	 * @param percentToRemove
	 *            0.0 - 1.0: the percent of the background which this algorithm should try to remove
	 * @return a background-subtracted list of values
	 */
	public static Spectrum calcBackgroundFunctionFit(ReadOnlySpectrum data, Spectrum function, float percentToRemove)
	{

		float value, minRatio, ratio;

		Spectrum result = new ISpectrum(data.size(), 0.0f);

		// start with the function *centred* at the 0 index, and go until it is at the last index
		for (int i = -(function.size() - 1); i < data.size(); i++)
		{

			minRatio = Float.MAX_VALUE;
			// go over every point in this function for its current position
			for (int j = 0; j < function.size(); j++)
			{

				if (i + j > 0 && i + j < data.size())
				{ // bounds check
					ratio = (data.get(i + j) * percentToRemove) / function.get(j);
					if (minRatio > ratio) minRatio = ratio;
				}

			}

			for (int j = 0; j < function.size(); j++)
			{

				value = function.get(j) * minRatio;

				if (i + j > 0 && i + j < data.size() && result.get(i + j) < value)
				{
					result.set(i + j, value);
				}

			}

		}

		return result;

	}
	
	@Override
	public String pluginUUID() {
		return "e9bfddb8-a8f0-490c-b01f-e2202581e809";
	}
	

}
