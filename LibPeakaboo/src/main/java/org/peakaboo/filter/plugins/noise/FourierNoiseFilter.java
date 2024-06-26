package org.peakaboo.filter.plugins.noise;



import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.FilterDescriptor;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.SelectionParameter;
import org.peakaboo.framework.autodialog.model.classinfo.EnumClassInfo;
import org.peakaboo.framework.autodialog.model.style.editors.DropDownStyle;
import org.peakaboo.framework.autodialog.model.style.editors.RealStyle;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;



/**
 * 
 * This class is a filter exposing the Fourier Low Pass functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */


public final class FourierNoiseFilter extends AbstractFilter {
	
	private Parameter<Float> startWavelength;
	private Parameter<Float> endWavelength;
	private SelectionParameter<FFT.FilterStyle> rolloff;


	public FourierNoiseFilter() {
		super();
	}
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String getFilterUUID() {
		return "68867e94-4e20-40a9-ba82-d7b1ad5a8af7";
	}
	
	@Override
	public void initialize() {
		
		rolloff = new SelectionParameter<>("Roll-Off Type", new DropDownStyle<>(), FFT.FilterStyle.LINEAR, new EnumClassInfo<>(FFT.FilterStyle.class), this::validate);
		rolloff.setPossibleValues(Arrays.asList(FFT.FilterStyle.values()));
		startWavelength = new Parameter<>("Starting Wavelength", new RealStyle(), 8f, this::validate);
		endWavelength = new Parameter<>("Ending Wavelength", new RealStyle(), 6f, this::validate);
		
		addParameter(rolloff, startWavelength, endWavelength);
	}
	

	private boolean validate(Parameter<?> p) {

		float start, end;
		boolean isCutoff = rolloff.getValue() == FFT.FilterStyle.CUTOFF;
		endWavelength.setEnabled(!isCutoff);

		start = startWavelength.getValue();
		if (start > 50 || start < 3) return false;

		end = endWavelength.getValue();
		if (end > 50 || end < 2) return false;

		if (!isCutoff && start < end) return false;

		return true;

	}


	@Override
	public String getFilterName() {
		return "Fourier Low-Pass";
	}


	@Override
	public FilterDescriptor getFilterDescriptor() {
		return FilterDescriptor.SMOOTHING;
	}


	@Override
	public String getFilterDescription() {
		return "The "
				+ getFilterName()
				+ " filter applies a Fourier transformation to the spectral data, converting it into the frequency domain. Data from a high frequency range (noise) is filtered out, while lower frequencies (peaks, background) are passed through.";
	}



	@Override
	protected SpectrumView filterApplyTo(SpectrumView data, Optional<FilterContext> ctx) {
		
		data = FFT.lowPassFilter(
			data,
			rolloff.getValue(),
			startWavelength.getValue(),
			endWavelength.getValue()
		);

		return data;
	}

	@Override
	public boolean canFilterSubset() {
		return false;
	}

}

class FFT {

	/**
	 * 
	 * Enumerates the ways in which the Fast Fourier Transform can work to eliminate high-frequency noise from
	 * a data set
	 * 
	 * @author Nathaniel Sherry
	 * 
	 */
	public enum FilterStyle {

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

	private static final FastFourierTransformer TRANSFORMER = new FastFourierTransformer(DftNormalization.STANDARD);

	
	public static Complex[] dataToFFT(SpectrumView data) {
		int n = data.size();
		float[] arr = ((Spectrum)data).backingArray();
		
        // Convert the float[] to double[] as that is the type accepted by the FFT function
        double[] complexInput = new double[n];
        for (int i = 0; i < n; i++) {
            complexInput[i] = arr[i];
        }
        
        // Perform FFT
        return TRANSFORMER.transform(complexInput, TransformType.FORWARD);
        
	}
	
	
	public static Spectrum fftToData(Complex[] fft) {
		int n = fft.length;
		
        // Perform inverse FFT to convert back to the energy domain
        Complex[] energyDomain = TRANSFORMER.transform(fft, TransformType.INVERSE);
		
        // Extract the real parts to get the smoothed signal
        float[] smoothedSignal = new float[n];
        for (int i = 0; i < n; i++) {
            smoothedSignal[i] = (float)energyDomain[i].getReal();
        }
		
        // Package and return the smoothed float[]
        return new ArraySpectrum(smoothedSignal);
        
	}
	
	
	/**
	 * Performs a Fast Fourier Transformation, and proceeds to remove high-frequency data.
	 * 
	 * @param data
	 *            the data to be filtered
	 * @param style
	 *            the {@link FilterStyle} which determines how the boundary between high-frequency and the rest
	 *            of the data is treated
	 * @param beginFilterAtWavelength
	 *            wavelength at which to begin filtering out noise
	 * @param endGradualFilterAtWavelength
	 *            when using a gradual fall-off method, the wavelength at above which to completely eliminate
	 *            high-frequency noise
	 * @return a Fast Fourier Transformation Low-Pass filtered data set
	 */
	public static Spectrum lowPassFilter(SpectrumView data, FilterStyle style, float startWavelength,
			float endWavelength)
	{

		int startcutoff, endcutoff;

		/*
		 * From JSci originally but appears to be true for Apache Commons Math as well
		 * [DFT is] an array containing the positive time part of the signal followed by the negative time part
		 * 
		 * So the highest frequency data is actually in the middle of the array.
		 * 
		 * The values are waves with phase+amplitude stored as complex numbers, where
		 * the array index k represents the frequency of the wave.
		 * 
		 * So we want to remove signal below a certain channel width. This requires 
		 * removing all signal with a wavelength below that size.
		 * 
		 * 
		 * 
		 * 2048 data points gets you: f = 1/2048, so l (wavelength) = 2048 f = 2/2048, so l = 1024
		 * 
		 * looking for ways to remove wavelengths less than minSignalWidth
		 * 
		 * 2048 / 2 = 1024 so data.size / cutoff = minSignalWidth data.size / minSignalWidth = cutoff
		 */

		
		
		
		int halfsize = (data.size() / 2);

		//wavelength of 4 has frequency of data.size() / 4
		int startFrequency = Math.round(data.size() / startWavelength);
		int endFrequency = Math.round(data.size() / endWavelength);
		
		startcutoff = Math.max(0, halfsize - startFrequency);
		endcutoff = Math.max(0, halfsize - endFrequency);
				
		return doFFTFilter(data, style, startcutoff, endcutoff);

	}


	private static Spectrum doFFTFilter(SpectrumView data, FilterStyle style, int start, int stop) {

		// FFT
		Complex[] transformedData = dataToFFT(data);


		// Do something with the transformed data
		if (style == FilterStyle.LINEAR) {
			fftLinearStyle(transformedData, start, stop);
		} else if (style == FilterStyle.SINE) {
			fftSineStyle(transformedData, start, stop);
		} else {
			fftCutoffStyle(transformedData, start);
		}


		// FFT Inverse Transform
		Spectrum result = fftToData(transformedData);

		// Apply a floor of 0 for all values
		for (int i = 0; i < data.size(); i++) {
			result.set(  i, Math.max(0f, result.get(i))  );
		}

		return result;

	}


	private static void fftCutoffStyle(Complex[] data, int start) {

		double centre = data.length / 2.0;
		for (int i = 0; i < data.length; i++) {

			if (i > Math.floor(centre - start) && i < Math.ceil(centre + start)) {
				data[i] = new Complex(0.0, 0.0);
			}

		}
	}


	private static void fftLinearStyle(Complex[] data, int start, int stop) {

		// start and stop are distances from the centrepoint, so start should be a higher number than stop

		double centre = data.length / 2.0;

		// start and stop as expressed by distances from center
		int di;

		double percentLeftInLine;


		for (int i = 0; i < data.length; i++) {

			di = (int) Math.abs(centre - i);


			// in between start and stop
			if (di < start && di > stop) {
				percentLeftInLine = 1.0 - ((double) (di - start) / (double) (stop - start));
				data[i] = new Complex(data[i].getReal() * percentLeftInLine, data[i].getImaginary() * percentLeftInLine);
			} else if (di < start) {

				data[i] = new Complex(0.0, 0.0);
			}



		}
	}


	private static void fftSineStyle(Complex[] data, int start, int stop) {

		// start and stop are distances from the centrepoint, so start should be a higher number than stop

		double centre = data.length / 2.0;

		// start and stop as expressed by distances from center
		int di;

		double percentLeftInLine;
		double sine;

		for (int i = 0; i < data.length; i++) {

			di = (int) Math.abs(centre - i);


			// in between start and stop
			if (di < start && di > stop) {
				percentLeftInLine = 1.0 - ((double) (di - start) / (double) (stop - start));
				sine = (Math.sin(Math.PI * percentLeftInLine - Math.PI / 2.0) + 1.0) / 2.0;
				data[i] = new Complex(data[i].getReal() * sine, data[i].getImaginary() * sine);
			} else if (di < start) {

				data[i] = new Complex(0.0, 0.0);
			}

		}

	}



}
