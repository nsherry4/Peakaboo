package peakaboo.calculations;



import java.util.List;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Spectrum;
import peakaboo.datatypes.functional.Function1;
import peakaboo.datatypes.functional.Functional;



/**
 * This section contains various methods of removing backound (low-frequency noise) from datasets. While these
 * algorithms are intended to be used on XRF data, they can probably be used with other types of data as well.
 * 
 * @author Nathaniel Sherry, 2009
 */
public class Background
{

	/**
	 * Fits a polynomial curve to the underside of the data for each data point, takes the union of the polynomials and
	 * subtracts that from the given data. This is a convenience method for
	 * {@link #removeBackgroundFunctionFit(List, List)}.
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
	public static Spectrum removeBackgroundPolynomial(Spectrum data, int width, int power, float percentToRemove)
	{

		// y = -(x * s)^power + m upside down parabola horizontally stretched by s and shifted upwards by m

		double centre = width / 2.0;
		double reach = 1.0 / centre;
		float value;
		double x;

		int raise = 1;
		if (power == 0) raise = 2;

		Spectrum function = new Spectrum(width);
		for (int i = 0; i < width; i++)
		{
			x = i - centre;
			value = (float) -Math.abs(Math.pow((x * reach), power)) + raise;
			function.set(i, value);
		}

		SpectrumCalculations.normalize_inplace(function);

		return removeBackgroundFunctionFit(data, function, percentToRemove);

	}


	/**
	 * Fits a given function to the underside of the data for each data point, takes the union of the fitted functions
	 * and subtracts that from the given data
	 * 
	 * @param data
	 *            the data to perform subtraction on
	 * @param function
	 *            the function (as a list of discreet values) to fit with
	 * @param percentToRemove
	 *            0.0 - 1.0: the percent of the background which this algorithm should try to remove
	 * @return a background-subtracted list of values
	 */
	public static Spectrum removeBackgroundFunctionFit(Spectrum data, Spectrum function, float percentToRemove)
	{

		float value, minRatio, ratio;

		Spectrum result = new Spectrum(data.size(), 0.0f);

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


	public static Spectrum removeBackgroundBrukner(Spectrum data, int windowSize, int repetitions)
	{

		// FIRST STEP
		float Iavg = SpectrumCalculations.sumValuesInList(data) / data.size();
		float Imin = SpectrumCalculations.min(data);
		float diff = Iavg - Imin;
		final float cutoff = Iavg + 2 * diff;

		Spectrum result = new Spectrum(data); 

		//initially cap the data at the given cutoff
		for (int i = 0; i < result.size(); i++)
		{
			if (result.get(i) > cutoff) result.set(i, cutoff);
		}
		
		Spectrum result2 = new Spectrum(result.size());

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


	public static void removeBackgroundBruknerIteration(final Spectrum source, final Spectrum target, final int windowSize)
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

}
