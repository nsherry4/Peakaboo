package org.peakaboo.filter.plugins.noise;



import org.peakaboo.filter.model.AbstractSimpleFilter;
import org.peakaboo.filter.model.FilterType;

import JSci.maths.wavelet.daubechies2.FastDaubechies2;
import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import net.sciencestudio.autodialog.model.Parameter;
import net.sciencestudio.autodialog.model.style.editors.IntegerStyle;



/**
 * This class is a filter exposing the Wavelet Noise Filter functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */


public final class WaveletNoiseFilter extends AbstractSimpleFilter
{

	private Parameter<Integer> passes;


	public WaveletNoiseFilter()
	{
		super();
	}
	
	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "0cb3b1d9-1ab6-46c0-adb7-72634c6ee595";
	}
	
	
	@Override
	public void initialize()
	{
		passes = new Parameter<>("Passes to Transform", new IntegerStyle(), 1, this::validate);
		addParameter(passes);
	}

	@Override
	public String getFilterName()
	{
		return "Wavelet Low-Pass";
	}


	@Override
	public FilterType getFilterType()
	{
		return FilterType.NOISE;
	}


	private boolean validate(Parameter<?> p)
	{
		int passCount;

		// remove largest, least significant passes from the wavelet transform
		// data
		// probably a bad idea to do more than 3 passes, but less than 1 is
		// senseless
		passCount = passes.getValue();
		if (passCount > 8 || passCount < 1) return false;

		return true;
	}


	@Override
	public String getFilterDescription()
	{
		return "The "
				+ getFilterName()
				+ " filter attempts to reduce high-frequency noise by performing a Wavelet transformation on the spectrum. This breaks the data down into sections each representing a different frequency range. The high-frequency regions are then smoothed, and a reverse transform is applied.";
	}


	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data)
	{
		Spectrum result;
		int passCount= passes.getValue();

		result = FWTLowPassFilter(data, passCount);

		return result;
	}
	
	@Override
	public boolean pluginEnabled()
	{
		return true;
	}


	@Override
	public boolean canFilterSubset()
	{
		return false;
	}



	/**
	 * Applies a Wavelet transform into a frequency domain and eliminates high-frequency noise
	 * @param data the data to be eliminated
	 * @param passesToRemove the number of sections to be removed, starting with the largest, highest-frequency section
	 * @return a Wavelet Low-Pass filtered dataset
	 */
	public static Spectrum FWTLowPassFilter(ReadOnlySpectrum data, int passesToRemove)
	{

		Spectrum result = new ISpectrum(data.size());

		float[] resultAsArray = data.backingArrayCopy();

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
	 * Transforms the given data to wavelet form
	 * @param data the data to transform
	 * @param steps the number of iterations to transform
	 * @return wavelet form data
	 */
	public static Spectrum DataToWavelet(ReadOnlySpectrum data, int steps)
	{
		Spectrum result = new ISpectrum(data.size());

		
		float[] dataAsArray = data.backingArrayCopy();

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
	public static Spectrum WaveletToData(ReadOnlySpectrum data, int steps)
	{
		Spectrum result = new ISpectrum(data.size());


		float[] dataAsArray = data.backingArrayCopy();

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
	
	
	

}
