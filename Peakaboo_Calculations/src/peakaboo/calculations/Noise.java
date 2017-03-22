package peakaboo.calculations;


import JSci.maths.Complex;
import JSci.maths.FourierMath;
import JSci.maths.polynomials.RealPolynomial;
import JSci.maths.wavelet.daubechies2.FastDaubechies2;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


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
	public static Spectrum MovingAverage(Spectrum data, int windowSpan)
	{

		Spectrum smoothed = new Spectrum(data.size());

		int start, stop;
		float sum;
		for (int i = 0; i < data.size(); i++) {

			// exact same as in last loop
			start = i - windowSpan;
			stop = i + windowSpan + 1;

			if (start < 0) start = 0;
			if (stop >= data.size()) stop = data.size() - 1;

			sum = SpectrumCalculations.sumValuesInList(data, start, stop);

			smoothed.set(i, sum / (stop - start));

		}


		return smoothed;

	}

	
	public static Complex[] DataToFFT(Spectrum data)
	{
		
		// Fast Fourier Transform

		double[] dataAsDoubles = new double[data.size()];

		for (int i = 0; i < data.size(); i++) {
			dataAsDoubles[i] = data.get(i);
		}


		// FFT Transform
		return FourierMath.transform(dataAsDoubles);
		
	}
	

	public static Spectrum FFTToData(Complex[] fft)
	{
		// FFT Inverse Transform
		fft = FourierMath.inverseTransform(fft);


		// get the data into a list of doubles for returning
		Spectrum result = new Spectrum(fft.length);
		for (int i = 0; i < fft.length; i++) {
			result.set(  i, Math.max(  0f, (float)(fft[i].real())  )  );
		}
		
		return result;
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
	public static Spectrum FFTLowPassFilter(Spectrum data, FFTStyle style, int beginFilterAtWavelength,
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


	private static Spectrum doFFTFilter(Spectrum data, FFTStyle style, int start, int stop)
	{

		// FFT
		Complex[] transformedData = DataToFFT(data);


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
		Spectrum result = new Spectrum(data.size());
		for (int i = 0; i < data.size(); i++) {
			result.set(  i, Math.max(0f, (float)transformedData[i].real())  );
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
				data[i] = new Complex(data[i].real() * percentLeftInLine, data[i].imag() * percentLeftInLine);
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
				data[i] = new Complex(data[i].real() * sine, data[i].imag() * sine);
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
	public static Spectrum FWTAgressiveLowPassFilter(Spectrum data, int passesToRemove)
	{

		Spectrum result = new Spectrum(data.size());

		float[] resultAsArray = data.toArray();

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

		for (int i = 0; i < data.size(); i++) {
			result.set(i, Math.max(0, resultAsArray[i]));
		}

		return result;

	}

	/**
	 * Transforms  the given data to wavelet form
	 * @param data the data to transform
	 * @param steps the number of iterations to transform
	 * @return wavelet form data
	 */
	public static Spectrum DataToWavelet(Spectrum data, int steps)
	{
		Spectrum result = new Spectrum(data.size());

		
		float[] dataAsArray = data.toArray();

		int lastSize = data.size();
		for (int i = 0; i < steps; i++) {
			FastDaubechies2.transform(dataAsArray, lastSize);
			lastSize /= 2;
		}
		
		// transform
		

		// back to list
		for (int i = 0; i < data.size(); i++) {
			result.set(i, dataAsArray[i]);
		}

		return result;
	}
	
	/**
	 * Transforms wavelet data back to normal
	 * @param data the wavelet data to untransform
	 * @param steps the number of iterations to untransform
	 * @return the untransformed data
	 */
	public static Spectrum WaveletToData(Spectrum data, int steps)
	{
		Spectrum result = new Spectrum(data.size());


		float[] dataAsArray = data.toArray();

		int lastSize = data.size();
		for (int i = 0; i < steps; i++) {
			lastSize /= 2;
		}
		
		for (int i = 0; i < steps; i++) {
			// inverse transform
			FastDaubechies2.invTransform(dataAsArray, lastSize);
			lastSize *= 2;
		}

		// back to list
		for (int i = 0; i < data.size(); i++) {
			result.set(i, dataAsArray[i]);
		}

		return result;
	}
	
	/**
	 * Applies a Wavelet transform into a frequency domain and attenuates high-frequency noise
	 * @param data the data to be eliminated
	 * @param stop the number of sections to be attenuated before the algoritm stops, starting with the largest, highest-frequency section
	 * @return a Wavelet Low-Pass filtered dataset
	 */
	public static Spectrum FWTLowPassFilter(Spectrum data, int stop)
	{

		Spectrum filter;
		Spectrum result = new Spectrum(data.size());


		float[] dataAsArray = data.toArray();
		int lastSize = data.size();

		// transform
		for (int i = 0; i < stop; i++) {
			FastDaubechies2.transform(dataAsArray, lastSize);
			lastSize /= 2;

			// into list for processing
			
			for (int j = 0; j < data.size(); j++) {
				result.set(i, dataAsArray[j]);
			}

			// FILTERING
			filter = result.subSpectrum(lastSize-1, lastSize * 2-1);
			//filter = SavitskyGolayFilter(filter, 1, 3, 0.0f, Float.MAX_VALUE);
			filter = SpringFilter(filter, 10, 2, 10);
			

			// out of list for processing
			for (int j = lastSize; j < lastSize * 2; j++) {
				dataAsArray[j] = filter.get(j - lastSize);
			}

		}


		// inverse transform
		for (int i = 0; i < stop; i++) {
			FastDaubechies2.invTransform(dataAsArray, lastSize);
			lastSize *= 2;
		}



		// back to list
		for (int i = 0; i < data.size(); i++) {
			result.set(i, Math.max(0, dataAsArray[i]));
		}

		return result;

	}


	/**
	 * Savitsky-Golay filter is like a moving average, but with higher order polynomials. <br>
	 * <br>
	 * Regular moving average can be seen as calculating a line which is fitted to the data points in
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
	public static Spectrum SavitskyGolayFilter(Spectrum data, int order, int reach, float min, float max)
	{

		
		Spectrum result = new Spectrum(data.size());

		RealPolynomial soln;

		double[] allDataAsArray = new double[data.size()];
		double[] indexAsArray = new double[reach * 2];
		double[][] dataAsArray = new double[2][reach * 2];
		
		
		for (int i = 0; i < data.size(); i++) {
			allDataAsArray[i] = data.get(i);
		}
		for (int i = 0; i < indexAsArray.length; i++) {
			indexAsArray[i] = i;
		}
		
		


		int subStart, subStop;
		
		boolean needsCustomArray = false;
		int customArraySize;
		for (int i = 0; i < data.size(); i++) {

			if (data.get(i) < min || data.get(i) > max)
			{
				result.set(i, data.get(i));
			}
			else
			{
				// exact same as in last loop
				subStart = i - reach;
				subStop = i + reach + 1;

				if (subStart < 0) 
				{
					subStart = 0;
					needsCustomArray = true;
				}
				if (subStop >= data.size()) 
				{
					subStop = data.size() - 1;
					needsCustomArray = true;
				}

				// pack the data into an array
				if (needsCustomArray)
				{
					customArraySize = subStop - subStart + 1;
					dataAsArray = new double[2][customArraySize];
					System.arraycopy(indexAsArray, 0, dataAsArray[0], 0, subStop - subStart - 1);
					
					
					if (customArraySize == reach*2) needsCustomArray = false;
					
					
				}

				//System.arraycopy(indexAsArray, 0, dataAsArray[0], 0, subStop - subStart - 1);
				System.arraycopy(allDataAsArray, subStart, dataAsArray[1], 0, subStop - subStart - 1);
			
				
				soln = JSci.maths.LinearMath.leastSquaresFit(order, dataAsArray);

				result.set(i, (float)Math.max(soln.map(reach), 0.0));
			}
			
		}


		return result;
	}
	
	
	/**
	 * The Spring filter is designed to smooth weaker data while preserving the structure of stronger signals.
	 * The Spring filter Filter operates on the assumption that weak signal should be smoothed more than strong signal.
	 * It treats each pair of points as if they were connected by a spring. With each iteration, a tension force draws
	 * neighbouring points closer together. The Force Multiplier controls how strongly the two elements are pulled 
	 * together, and the Force Falloff Rate controls how aggressively stronger signal is anchored in place, unmoved 
	 * by spring forces. This prevents peaks from being distorted by the smoothing algorithm.
	 * @param data the {@link Spectrum} to smooth
	 * @param forceMultiplier the linear force multiplier value
	 * @param falloffExp the exponential force falloff value
	 * @param iterations the number of iterations to perform the smoothing
	 * @return the smoothed data
	 */
	public static Spectrum SpringFilter(Spectrum data, float forceMultiplier, float falloffExp, int iterations)
	{
		Spectrum result = new Spectrum(data);
		
		for (int i = 0; i < iterations; i++)
		{
			SpringFilterIteration(result, forceMultiplier, falloffExp);
		}
		
		return result;

	}
	
	private static void SpringFilterIteration(Spectrum data, float forceMultiplier, float falloffExp)
	{

	
		Spectrum deltas = deriv(data);
		
		Spectrum forces = new Spectrum(data.size());
		
		//calculate the forces for each point
		//forces represent how much pull a points neighbours are exerting on it.
		//the further away its neighbours are, the more the "spring" has streched, and
		//the stronger the force will be.
		//Then, we want to make sure that peaks aren't distorted, so we reduce the force
		//as the signal gets stronger. This fits with the assumption that weaker signal will be
		//noisier.
		float dist, force;
		for (int i = 0; i < forces.size(); i++)
		{
			
			if (i == 0) 						force = -deltas.get(0) / 2.0f;
			else if (i == forces.size() - 1)  	force = deltas.get(deltas.size()-1) / 2.0f;
			else 								force = (deltas.get(i-1) + (-deltas.get(i))) / 4.0f; 

			//if dist dips below 0 and we use a falloff exp like 1.6, we'd like to get a sensible answer, rather than NaN
			dist = Math.abs(data.get(i));
			dist = (float) Math.pow(dist, falloffExp);
			if (dist < 1) dist = 1f;
			
			//distFromAverage = 1f;
			if (force < 0)
			{
				force = Math.max(force,  (force / dist) * forceMultiplier);
			} else {
				force = Math.min(force, (force / dist) * forceMultiplier);
			}
			
			data.set(i, data.get(i) - force);
			
		}
		
	}

	
	/**
	 * Calculates the derivitive (deltas) for a spectrum
	 * @param list the data to find the deltas for
	 * @return a list of deltas
	 */
	public static Spectrum deriv(Spectrum list)
	{
	
		Spectrum result = new Spectrum(list.size());
		
		result.add(list.get(0));
		for (int i = 0; i < list.size()-1; i++)
		{
			result.set(i, list.get(i+1) - list.get(i));
		}
			
		return result;
		
	}
	
	/**
	 * Calculates the integral (sums up to X) for a spectrum
	 * @param list the data to find the integral for
	 * @return a list of sums
	 */
	public static Spectrum integ(Spectrum list)
	{
		
		Spectrum result = new Spectrum(list.size());
		float val = 0;
		
		
		for (int i = 0; i < list.size(); i++)
		{
			val += list.get(i);
			result.set(i,  val );
		}
		
		return result;
		
	}
	
}
