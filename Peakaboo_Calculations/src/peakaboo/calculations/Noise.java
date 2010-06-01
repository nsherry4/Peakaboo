package peakaboo.calculations;


import java.util.List;

import JSci.maths.Complex;
import JSci.maths.FourierMath;
import JSci.maths.polynomials.RealPolynomial;
import JSci.maths.wavelet.daubechies2.FastDaubechies2;

import peakaboo.datatypes.DataTypeFactory;

/**
 * 
 * 
 * This class contains a collection of methods designed to help eliminate high-frequency noise from a data
 * set.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class Noise
{

	/**
	 * 
	 * Enumerates the ways in which the Fast Fourier Transform can work to eliminate high-frequency noise from
	 * a data set
	 * 
	 * @author Nathaniel Sherry
	 * 
	 */
	public enum FFTStyle
	{

		CUTOFF {

			@Override
			public String toString()
			{
				return "Cutoff";
			}
		},
		LINEAR {

			@Override
			public String toString()
			{
				return "Linear Roll-Off";
			}
		},
		SINE {

			@Override
			public String toString()
			{
				return "Sinusoidal Roll-Off";
			}
		}
	}


	/**
	 * 
	 * Performs a straight moving average over a data set
	 * 
	 * @param data
	 *            the data to be averaged
	 * @param windowSpan
	 *            the distance from the centrepoint to an edge of the set of numbers being averaged
	 * @return a moving-average smoothed data set
	 */
	public static List<Double> MovingAverage(List<Double> data, int windowSpan)
	{

		List<Double> smoothed = DataTypeFactory.<Double> list(data.size());
		List<Double> area;


		int subStart, subStop;
		double sum;
		for (int i = 0; i < data.size(); i++) {

			// exact same as in last loop
			subStart = i - windowSpan;
			subStop = i + windowSpan + 1;

			if (subStart < 0) subStart = 0;
			if (subStop >= data.size()) subStop = data.size() - 1;

			area = data.subList(subStart, subStop);

			sum = 0;
			for (double d : area) {
				sum += d;
			}

			smoothed.add(i, sum / area.size());

		}


		return smoothed;

	}


	/**
	 * Performs a Fast Fourier Transformation, and proceeds to remove high-frequency data.
	 * 
	 * @param data
	 *            the data to be filtered
	 * @param style
	 *            the {@link FFTStyle} which determines how the boundary between high-frequency and the rest
	 *            of the data is treated
	 * @param beginFilterAtWavelength
	 *            wavelength at which to begin filtering out noise
	 * @param endGradualFilterAtWavelength
	 *            when using a gradual fall-off method, the wavelength at above which to completely eliminate
	 *            high-frequency noise
	 * @return a Fast Fourier Transformation Low-Pass filtered data set
	 */
	public static List<Double> FFTLowPassFilter(List<Double> data, FFTStyle style, int beginFilterAtWavelength,
			int endGradualFilterAtWavelength)
	{

		int startcutoff, endcutoff;

		/*
		 * 2048 data points gets you: f = 1/2048, so l (wavelength) = 2048 f = 2/2048, so l = 1024
		 * 
		 * looking for ways to remove wavelengths less than minSignalWidth
		 * 
		 * 2048 / 2 = 1024 so data.size / cutoff = minSignalWidth data.size / minSignalWidth = cutoff
		 */

		startcutoff = (data.size() / 2) - (int) ((double) data.size() / (double) beginFilterAtWavelength);
		endcutoff = (data.size() / 2) - (int) ((double) data.size() / (double) endGradualFilterAtWavelength);

		return doFFTFilter(data, style, startcutoff, endcutoff);
		// return getFFTBandstopFilter(data, cutoff, 0);

	}


	private static List<Double> doFFTFilter(List<Double> data, FFTStyle style, int start, int stop)
	{

		// Fast Fourier Transform

		double[] dataAsArray = new double[data.size()];
		Complex[] transformedData;

		for (int i = 0; i < data.size(); i++) {
			dataAsArray[i] = data.get(i);
		}


		// FFT Transform
		transformedData = FourierMath.transform(dataAsArray);


		// Do something with the transformed data
		if (style == FFTStyle.LINEAR) {
			FFTLinearStyle(transformedData, start, stop);
		} else if (style == FFTStyle.SINE) {
			FFTSineStyle(transformedData, start, stop);
		} else {
			FFTCutoffStyle(transformedData, start);
		}


		// FFT Inverse Transform
		transformedData = FourierMath.inverseTransform(transformedData);


		// get the data into a list of doubles for returning
		List<Double> result = DataTypeFactory.<Double> list(data.size());
		for (int i = 0; i < data.size(); i++) {
			result.add(i, transformedData[i].real());
		}

		return result;

	}


	private static void FFTCutoffStyle(Complex[] data, int start)
	{

		double centre = data.length / 2.0;
		for (int i = 0; i < data.length; i++) {

			if (i > Math.floor(centre - start) && i < Math.ceil(centre + start)) {
				data[i] = new Complex(0.0, 0.0);
			}

		}
	}


	private static void FFTLinearStyle(Complex[] data, int start, int stop)
	{

		// start and stop are distances from the centrepoint, so start should be a higher number than stop

		double centre = data.length / 2.0;

		// start and stop as expressed by distances from center
		int di;

		double percentLeftInLine = 0.0;


		for (int i = 0; i < data.length; i++) {

			di = (int) Math.abs(centre - i);


			// in between start and stop
			if (di < start && di > stop) {
				percentLeftInLine = 1.0 - ((double) (di - start) / (double) (stop - start));
				data[i] = new Complex(data[i].real() * percentLeftInLine, data[i].imag());
			} else if (di < start) {

				data[i] = new Complex(0.0, 0.0);
			}



		}
	}


	private static void FFTSineStyle(Complex[] data, int start, int stop)
	{

		// start and stop are distances from the centrepoint, so start should be a higher number than stop

		double centre = data.length / 2.0;

		// start and stop as expressed by distances from center
		int di;

		double percentLeftInLine = 0.0;
		double sine;

		for (int i = 0; i < data.length; i++) {

			di = (int) Math.abs(centre - i);


			// in between start and stop
			if (di < start && di > stop) {
				percentLeftInLine = 1.0 - ((double) (di - start) / (double) (stop - start));
				sine = (Math.sin(Math.PI * percentLeftInLine - Math.PI / 2.0) + 1.0) / 2.0;
				data[i] = new Complex(data[i].real() * sine, data[i].imag());
			} else if (di < start) {

				data[i] = new Complex(0.0, 0.0);
			}

		}

	}



	/**
	 * Applies a Wavelet transform into a frequency domain and eliminates high-frequency noise
	 * @param data the data to be eliminated
	 * @param passesToRemove the number of sections to be removed, starting with the largest, highest-frequency section
	 * @return a Wavelet Low-Pass filtered dataset
	 */
	public static List<Double> FWTAgressiveLowPassFilter(List<Double> data, int passesToRemove)
	{

		List<Double> result = DataTypeFactory.<Double> list(data.size());

		float[] resultAsArray = new float[data.size()];
		for (int i = 0; i < data.size(); i++) {
			resultAsArray[i] = (float) data.get(i).doubleValue();
		}

		FastDaubechies2 fwt = new FastDaubechies2();


		// to wavelet space
		fwt.transform(resultAsArray);


		// from the number of passes to keep, calculate the number of channels that should stay
		int channelsToKeep = data.size();
		for (int i = 0; i < passesToRemove; i++) {
			channelsToKeep /= 2;
		}
		// clear everything after the determined number of channels
		for (int i = channelsToKeep; i < data.size(); i++) {
			resultAsArray[i] = 0.0f;
		}


		// and back to energy space
		fwt.invTransform(resultAsArray);

		result.clear();
		for (int i = 0; i < data.size(); i++) {
			result.add((double) resultAsArray[i]);
		}

		return result;

	}

	/**
	 * Applies a Wavelet transform into a frequency domain and attenuates high-frequency noise
	 * @param data the data to be eliminated
	 * @param stop the number of sections to be attenuated before the algoritm stops, starting with the largest, highest-frequency section
	 * @return a Wavelet Low-Pass filtered dataset
	 */
	public static List<Double> FWTLowPassFilter(List<Double> data, int stop)
	{

		List<Double> filter;
		List<Double> result = DataTypeFactory.<Double> list(data.size());


		float[] dataAsArray = new float[data.size()];



		for (int i = 0; i < data.size(); i++) {
			dataAsArray[i] = (float) data.get(i).doubleValue();
		}


		int lastSize = data.size();

		// transform
		for (int i = 0; i < stop; i++) {
			FastDaubechies2.transform(dataAsArray, lastSize);
			lastSize /= 2;

			// into list for processing
			result.clear();
			for (int j = 0; j < data.size(); j++) {
				result.add((double) dataAsArray[j]);
			}

			// FILTERING
			filter = result.subList(lastSize, lastSize * 2);
			filter = SavitskyGolayFilter(filter, 1, 3, 0.0, Double.MAX_VALUE);

			// out of list for processing
			for (int j = lastSize; j < lastSize * 2; j++) {
				dataAsArray[j] = (float) filter.get(j - lastSize).doubleValue();
			}

			result.clear();

		}


		// inverse transform
		for (int i = 0; i < stop; i++) {
			FastDaubechies2.invTransform(dataAsArray, lastSize);
			lastSize *= 2;
		}



		// back to list
		result.clear();
		for (int i = 0; i < data.size(); i++) {
			result.add((double) dataAsArray[i]);
		}

		return result;

	}


	/**
	 * Savitsky-Golay filter is like a moving average, but with higher order polynomials. <br>
	 * <br>
	 * Regular moving average can be seen as calculating a line/slope which is fitted to the data points in
	 * the moving average window, and then taking the value of the line at the centre-point (where the data
	 * point being averaged is)<br>
	 * <br>
	 * This routine fits a higher order polynomial to the data in the averaging window, and determines the
	 * value of the centre-point just as before.<br>
	 * <br>
	 * This has the advantage of preserving the shapes of peaks much better, because lower points on either
	 * side don't result in the average-line being lowered, but can be represented much more accurately by
	 * using higher order polynomials such as a parabola, which won't truncate the peak in nearly as drastic a
	 * way.
	 * 
	 * @param data the data to be smoothed
	 * @param order the power/order of the polynomial to fit to each section of the  data
	 * @param reach the distance from the centrepoint to the edge of the data being considered in a fitting
	 * @return a Savitsky-Golay smoothed data set.
	 */
	public static List<Double> SavitskyGolayFilter(List<Double> data, int order, int reach, double min, double max)
	{

		
		List<Double> result = DataTypeFactory.<Double> list(data.size());

		RealPolynomial soln;

		double[] allDataAsArray = new double[data.size()];
		double[] indexAsArray = new double[reach * 2 + 2];
		for (int i = 0; i < data.size(); i++) {
			allDataAsArray[i] = data.get(i);
		}
		for (int i = 0; i < indexAsArray.length; i++) {
			indexAsArray[i] = i;
		}
		
		


		int subStart, subStop;
		for (int i = 0; i < data.size(); i++) {

			if (data.get(i) < min || data.get(i) > max)
			{
				result.add(data.get(i));
			}
			else
			{
				// exact same as in last loop
				subStart = i - reach;
				subStop = i + reach + 1;

				if (subStart < 0) subStart = 0;
				if (subStop >= data.size()) subStop = data.size() - 1;

				// pack the data into an array
				double[][] dataAsArray = new double[2][subStop - subStart + 1];
				// for (int j = subStart; j <= subStop; j++){
				// dataAsArray[0][j - subStart] = j - subStart;
				// dataAsArray[1][j - subStart] = data.get(j);
				// }
				System.arraycopy(indexAsArray, 0, dataAsArray[0], 0, subStop - subStart + 1);
				System.arraycopy(allDataAsArray, subStart, dataAsArray[1], 0, subStop - subStart + 1);

				soln = JSci.maths.LinearMath.leastSquaresFit(order, dataAsArray);

				result.add(Math.max(soln.map(reach), 0.0));
			}
			
		}


		return result;
	}

	
	public static List<Double> deriv(List<Double> list)
	{
	
		List<Double> result = DataTypeFactory.<Double>list();
		
		result.add(list.get(0));
		for (int i = 0; i < list.size()-1; i++)
		{
			result.add(list.get(i+1) - list.get(i));
		}
			
		return result;
		
	}
	
	public static List<Double> integ(List<Double> list)
	{
		
		List<Double> result = DataTypeFactory.<Double>list();
		double val = 0;
		
		
		for (int i = 0; i < list.size(); i++)
		{
			val += list.get(i);
			result.add( val );
		}
		
		return result;
		
	}
	
}
