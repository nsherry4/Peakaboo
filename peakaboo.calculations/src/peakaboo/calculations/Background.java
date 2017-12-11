package peakaboo.calculations;




import scitypes.ISpectrum;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;




/**
 * This section contains various methods of removing backound (low-frequency noise) from datasets. While these
 * algorithms are intended to be used on XRF data, they can probably be used with other types of data as well.
 * 
 * @author Nathaniel Sherry, 2009-2010
 */
public class Background
{

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
	public static Spectrum calcBackgroundParabolic(Spectrum data, int width, int power, float percentToRemove)
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
	public static Spectrum calcBackgroundFunctionFit(Spectrum data, Spectrum function, float percentToRemove)
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
	public static Spectrum calcBackgroundBrukner(Spectrum data, int windowSize, int repetitions)
	{

		// FIRST STEP
		float Iavg = SpectrumCalculations.sumValuesInList(data) / data.size();
		float Imin = SpectrumCalculations.min(data);
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
	
	
	
	
	/**
	 * The Linear Trim background removal algorithm works by defining a sequence of line segments between
	 * each pair of data points m point apart. (ie (1,5), (2,6), (3, 7) if m = 4) with the height of each
	 * end of the line segment being the height of the signal at that point. Any values in the source data
	 * between those two points which exceed the height of the line segment are cropped.  
	 * @param scan the source data to calculate the background of
	 * @param lineSize the length of the line segments to be generated (m, from the description above)
	 * @param iterations the number of iterations of this algorithm to run.
	 * @return
	 */
	public static Spectrum calcBackgroundLinearTrim(final Spectrum scan, int lineSize, int iterations)
	{
		
		
		Spectrum result1 = new ISpectrum(scan);
		Spectrum result2 = new ISpectrum(scan.size());
		Spectrum temp;
		Spectrum lineSegment = new ISpectrum(scan.size());
		
		for (int i = 0; i < iterations; i++)
		{
			result2.copy(result1);
			calcBackgroundLinearTrimIteration(result1, result2, lineSegment, lineSize);
			temp = result2;
			result2 = result1;
			result1 = temp;
		}
		
		return result1;
		
	}
	
	/**
	 * Runs an individual iteration of the Linear Trim background removal technique
	 * @param scan the source data to from which we calculate the background
	 * @param target the target to which we write the results
	 * @param lineSegment a {@link Spectrum} in which we can store the height/intensity values of a line segment.
	 * @param lineSize the length of the line segments
	 * @return
	 */
	private static Spectrum calcBackgroundLinearTrimIteration(final Spectrum scan, final Spectrum target, final Spectrum lineSegment, int lineSize)
	{
		int first = -lineSize+1;
		int last = 0;
		
		int boundedFirst, boundedLast;

		while(first < scan.size()-1)
		{
			
			boundedFirst = Math.max(first, 0);
			boundedLast = Math.min(last, scan.size()-1);
			
			linearTrimLinearSegment(lineSegment, scan.get(boundedFirst), scan.get(boundedLast), boundedFirst, boundedLast);
			
			linearTrimCommitLinearSegment(target, lineSegment, boundedFirst, boundedLast);
			
			first++;
			last++;
			
			
		}
		
		
		return target;
		
		
	}
	
	
	/**
	 * Generate a line segment from the given parameters
	 * @param target the {@link Spectrum} that we write the line segment to
	 * @param start the start value (ie height, intensity) for this line segment
	 * @param stop the stop value (ie height, intensity) for this line segment
	 * @param startIndex the index where the line segment begins
	 * @param stopIndex the index where the line segment ends
	 * @return target
	 */
	private static Spectrum linearTrimLinearSegment(Spectrum target, float start, float stop, int startIndex, int stopIndex)
	{
	
		float span = (stopIndex) - startIndex;
		float delta = stop - start;
		float percent;
		
		for (int i = startIndex; i <= stopIndex; i++)
		{
			percent = (i-startIndex) / span;
			target.set(i, start + delta*percent);
		}
		
		return target;
		
	}
	
	/**
	 * Takes a data {@link Spectrum} and a Spectrum containing a line segment, and sets the values in the data spectrum to the values
	 * in the line segment spectrum if the line segment value is lower, but >=0
	 * @param data the Spectrum containing the data
	 * @param lineSegment the Spectrum containing the line segment
	 * @param startIndex the index to start at
	 * @param stopIndex the index to stop at
	 * @return
	 */
	private static Spectrum linearTrimCommitLinearSegment(Spectrum data, Spectrum lineSegment, int startIndex, int stopIndex)
	{
		float datapoint;
		float linepoint;
		
		for (int i = startIndex; i <= stopIndex; i++)
		{
			datapoint = data.get(i);
			linepoint = lineSegment.get(i);
			
			if (datapoint > linepoint && linepoint >= 0)
			{
				data.set(i, linepoint);
			}
		}
		return data;
	}
	
	

}
