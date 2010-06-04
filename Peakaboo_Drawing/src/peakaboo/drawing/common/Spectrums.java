package peakaboo.drawing.common;



import java.awt.Color;
import java.util.List;

import peakaboo.datatypes.DataTypeFactory;



/**
 * This class provides methods for generating Color spectrums for displaying data.
 * 
 * @author Nathaniel Sherry, 2009
 */

public class Spectrums
{

	public static int	DEFAULT_STEPS	= 1000;


	/**
	 * Creates a thermal scale spectrum
	 * 
	 * @param steps
	 *            the number of steps in the spectrum
	 * @return a thermal scale spectrum
	 */
	public static List<Color> ThermalScale(int steps)
	{
		return getSmallerSpectrum(steps, ThermalScale());
	}


	/**
	 * Creates a thermal ratio scale spectrum
	 * 
	 * @param steps
	 *            the number of steps in the spectrum
	 * @return a thermal scale spectrum
	 */
	public static List<Color> RatioThermalScale(int steps)
	{
		return getSmallerSpectrum(steps, RatioThermalScale());
	}


	/**
	 * Creates a monochrome scale spectrum
	 * 
	 * @param steps
	 *            the number of steps in the spectrum
	 * @return a thermal scale spectrum
	 */
	public static List<Color> MonochromeScale(int steps)
	{
		return getSmallerSpectrum(steps, MonochromeScale());
	}
	
	/**
	 * Creates a monochrome scale spectrum
	 * 
	 * @param steps
	 *            the number of steps in the spectrum
	 * @param c
	 *            the colour of the monochrome scale
	 */
	public static List<Color> MonochromeScale(int steps, Color c)
	{
		return getSmallerSpectrum(steps, MonochromeScale(c));
	}


	/**
	 * Creates a monochrome ratio scale spectrum
	 * 
	 * @param steps
	 *            the number of steps in the spectrum
	 * @return a thermal scale spectrum
	 */
	public static List<Color> RatioMonochromeScale(int steps)
	{
		return getSmallerSpectrum(steps, RatioMonochromeScale());
	}


	/**
	 * Creates a thermal scale spectrum with {@link #DEFAULT_STEPS} steps
	 * 
	 * @return a new thermal scale
	 */
	public static List<Color> ThermalScale()
	{

		float steps = DEFAULT_STEPS;

		int[] intervals = { Math.round(0.15f * steps), Math.round(0.35f * steps), Math.round(0.15f * steps),
				Math.round(0.15f * steps), Math.round(0.20f * steps) };
		double[][] values = { { 0.07, 0.16, 0.30 }, { 0.13, 0.29, 0.53 }, { 0.35, 0.69, 0.03 }, { 0.89, 0.73, 0.00 },
				{ 0.81, 0.36, 0.00 }, { 0.64, 0.00, 0.00 } };

		return generateSpectrum(intervals, values);
	}


	/**
	 * Creates a ratio(red/blue) thermal scale spectrum with {@link #DEFAULT_STEPS} steps
	 * 
	 * @return a new thermal scale
	 */
	public static List<Color> RatioThermalScale()
	{

		float steps = DEFAULT_STEPS;

		int[] intervals = { Math.round(0.30f * steps), Math.round(0.20f * steps), Math.round(0.20f * steps),
				Math.round(0.30f * steps) };
		double[][] values = { { 0.125, 0.29, 0.53 }, { 0.063, 0.145, 0.265 }, { 0.0, 0.0, 0.0 }, { 0.32, 0.0, 0.0 },
				{ 0.64, 0.0, 0.0 } };

		return generateSpectrum(intervals, values);

	}


	/**
	 * Creates a monochrome scale spectrum
	 * 
	 * @param steps
	 *            the number of steps in the spectrum
	 * @return a monochrome scale spectrum
	 */
	public static List<Color> MonochromeScale()
	{

		float steps = DEFAULT_STEPS;

		int[] intervals = { Math.round(1.0f * steps) };
		double[][] values = { { 0.0, 0.0, 0.0 }, { 1.0, 1.0, 1.0 } };

		return generateSpectrum(intervals, values);

	}
	
	/**
	 * Creates a monochrome scale spectrum ranging from black to the given colour 
	 * 
	 * @param c
	 *            the colour of the monochrome scale
	 * @return a monochrome scale spectrum
	 */
	public static List<Color> MonochromeScale(Color c)
	{

		float steps = DEFAULT_STEPS;

		int[] intervals = { Math.round(1.0f * steps) };
		double[][] values = { { 0.0, 0.0, 0.0 }, { c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0 } };

		return generateSpectrum(intervals, values);

	}


	/**
	 * Creates a ratio(red/blue) thermal scale spectrum with {@link #DEFAULT_STEPS} steps
	 * 
	 * @return a new thermal scale
	 */
	public static List<Color> RatioMonochromeScale()
	{

		float steps = DEFAULT_STEPS;

		int[] intervals = { Math.round(0.35f * steps), Math.round(0.15f * steps), Math.round(0.15f * steps),
				Math.round(0.35f * steps) };
		double[][] values = { { 0.0, 0.0, 0.0 }, { 0.2, 0.2, 0.2 }, { 0.5, 0.5, 0.5 }, { 0.8, 0.8, 0.8 },
				{ 1.0, 1.0, 1.0 } };

		return generateSpectrum(intervals, values);

	}


	/**
	 * Creates a thermal scale spectrum
	 * 
	 * @param steps
	 *            the number of steps in the spectrum
	 * @return a radio monochrome scale spectrum
	 */
	public static List<Color> getSmallerSpectrum(int steps, List<Color> fullSpectrum)
	{
		List<Color> spectrum = DataTypeFactory.<Color> list();

		double interval = (DEFAULT_STEPS - 1) / (double) steps;
		double percentInSpectrum;
		int spotInSpectrum;

		for (int i = 0; i < steps; i++)
		{

			percentInSpectrum = i / (double) (steps - 1);
			spotInSpectrum = (int) Math.round((i + percentInSpectrum) * interval);

			spectrum.add(fullSpectrum.get(spotInSpectrum));

		}

		return spectrum;

	}


	public static List<Color> generateSpectrum(int[] intervals, double[][] values)
	{
		double steps = DEFAULT_STEPS;

		List<Color> spectrum = DataTypeFactory.<Color> list();

		int realSteps = 0;
		for (int i = 0; i < intervals.length; i++)
		{
			realSteps += intervals[i];
		}
		intervals[intervals.length - 1] += (steps - realSteps);

		double red, blue, green;
		int interval;
		double percent;

		// for each entry in the intervals[], defining the size of the stretch of Color given in values[][]
		for (int stage = 0; stage < intervals.length; stage++)
		{

			interval = intervals[stage];

			for (int step = 0; step < interval; step++)
			{
				percent = (double) step / (double) interval;

				red = values[stage][0] * (1.0 - percent) + values[stage + 1][0] * percent;
				green = values[stage][1] * (1.0 - percent) + values[stage + 1][1] * percent;
				blue = values[stage][2] * (1.0 - percent) + values[stage + 1][2] * percent;

				spectrum.add(new Color((float) red, (float) green, (float) blue));
			}

		}

		return spectrum;
	}

}