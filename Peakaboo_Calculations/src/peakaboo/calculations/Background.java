package peakaboo.calculations;


import java.util.List;

import peakaboo.calculations.functional.Function1;
import peakaboo.calculations.functional.Functional;
import peakaboo.datatypes.DataTypeFactory;

/**
 * 
 * This section contains various methods of removing backound (low-frequency noise) from datasets. While these
 * algorithms are intended to be used on XRF data, they can probably be used with other types of data as well.
 * 
 * @author Nathaniel Sherry, 2009
 */
public class Background
{

	/**
	 * Fits a polynomial curve to the underside of the data for each data point, takes the union of the
	 * polynomials and subtracts that from the given data. This is a convenience method for
	 * {@link #removeBackgroundFunctionFit(List, List)}.
	 * 
	 * @param data
	 *            the data to perform subtraction on
	 * @param width
	 *            the width of the polynomial
	 * @param power
	 *            the power/order of the polynomial
	 * @param percentToRemove 0.0 - 1.0: the percent of the background which this algorithm should try to remove
	 * @return a background-subtracted list of values
	 */
	public static List<Double> removeBackgroundPolynomial(List<Double> data, int width, int power, double percentToRemove)
	{

		// y = -(x * s)^power + m upside down parabola horizontally stretched by s and shifted upwards by m

		double centre = width / 2.0;
		double reach = 1.0 / centre;
		double value;
		double x;

		int raise = 1;
		if (power == 0) raise = 2;

		List<Double> function = DataTypeFactory.<Double> list(width);
		for (int i = 0; i < width; i++) {
			x = i - centre;
			value = -Math.abs(Math.pow((x * reach), power)) + raise;
			function.add(value);
		}

		ListCalculations.normalize_inplace(function);

		return removeBackgroundFunctionFit(data, function, percentToRemove);

	}


	/**
	 * Fits a given function to the underside of the data for each data point, takes the union of the fitted
	 * functions and subtracts that from the given data
	 * 
	 * @param data
	 *            the data to perform subtraction on
	 * @param function
	 *            the function (as a list of discreet values) to fit with
	 * @param percentToRemove 0.0 - 1.0: the percent of the background which this algorithm should try to remove
	 * @return a background-subtracted list of values
	 */
	public static List<Double> removeBackgroundFunctionFit(List<Double> data, List<Double> function, double percentToRemove)
	{


		double value, minRatio, ratio;

		List<Double> result = DataTypeFactory.<Double> list(data.size());

		// initialize
		for (int i = 0; i < data.size(); i++) {
			result.add(0.0);
		}


		//start with the function *centred* at the 0 index, and go until it is at the last index
		for (int i = -(function.size() - 1); i < data.size(); i++) { 

			minRatio = Double.MAX_VALUE;
			//go over every point in this function for its current position
			for (int j = 0; j < function.size(); j++) {

				if (i + j > 0 && i + j < data.size()) { //bounds check
					ratio = ( data.get(i + j) * percentToRemove ) / function.get(j);
					if (minRatio > ratio) minRatio = ratio;
				}

			}

			for (int j = 0; j < function.size(); j++) {

				value = function.get(j) * minRatio;

				if (i + j > 0 && i + j < data.size() && result.get(i + j) < value) {
					result.set(i + j, value);
				}

			}

		}

		return result;

	}

	
	
	
	public static List<Double> removeBackgroundBrukner(List<Double> data, int windowSize, int repetitions)
	{
		
		//FIRST STEP
		double Iavg = ListCalculations.sumValuesInList(data) / data.size();
		double Imin = ListCalculations.min(data);
		double diff = Iavg - Imin;
		final double cutoff = Iavg + 2*diff;
		
		
		List<Double> result = Functional.map(data, new Function1<Double, Double>() {
			@Override
			public Double f(Double element) { return (element <= cutoff) ? element : cutoff; }
		});
		
		List<Double> result2 = DataTypeFactory.<Double>list(result.size());
		
		
		int i = 0;
		while(repetitions > 0)
		{
			removeBackgroundBruknerIteration(result, result2, windowSize);
			i++; 
			if (i > repetitions){
				result = result2;
				break;
			}
			
			removeBackgroundBruknerIteration(result2, result, windowSize);
			i++; if (i > repetitions) break;
			
		}
		
		return result;
		
		
	}
	
	public static void removeBackgroundBruknerIteration(final List<Double> data, final List<Double> target, final int windowSize)
	{
		Functional.map_index_target(data, target, new Function1<Integer, Double>() {

			@Override
			public Double f(Integer element) {
				int start, stop;
				start = Math.max(element-windowSize, 0);
				stop = Math.min(element+windowSize, data.size()-1);
				List<Double> range = data.subList(start, stop);
				double average = ListCalculations.sumValuesInList(range) / range.size();
				return Math.min(average, data.get(element));
			}
		});
		
	}
	
	
	
}
