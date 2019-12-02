package org.peakaboo.filter.plugins.noise;



import org.peakaboo.dataset.DataSet;
import org.peakaboo.filter.model.AbstractFilter;
import org.peakaboo.filter.model.FilterType;
import org.peakaboo.framework.autodialog.model.Parameter;
import org.peakaboo.framework.autodialog.model.style.editors.IntegerStyle;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.Spectrum;

import JSci.maths.wavelet.daubechies2.FastDaubechies2;



/**
 * This class is a filter exposing the Wavelet Noise Filter functionality elsewhere in this programme.
 * 
 * @author Nathaniel Sherry, 2009
 */


public final class WaveletNoiseFilter extends AbstractFilter
{

	private Parameter<Integer> passes;


	public WaveletNoiseFilter() {
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
	public void initialize() {
		passes = new Parameter<>("Passes to Transform", new IntegerStyle(), 1, this::validate);
		addParameter(passes);
	}

	@Override
	public String getFilterName() {
		return "Wavelet Low-Pass";
	}


	@Override
	public FilterType getFilterType() {
		return FilterType.NOISE;
	}


	private boolean validate(Parameter<?> p) {
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
	public String getFilterDescription() {
		return "The "
				+ getFilterName()
				+ " filter attempts to reduce high-frequency noise by performing a Wavelet transformation on the spectrum. This breaks the data down into sections each representing a different frequency range. The high-frequency regions are then smoothed, and a reverse transform is applied.";
	}


	@Override
	protected ReadOnlySpectrum filterApplyTo(ReadOnlySpectrum data, DataSet dataset) {
		Spectrum result;
		int passCount= passes.getValue();

		result = FWTLowPassFilter(data, passCount);

		return result;
	}
	
	@Override
	public boolean pluginEnabled() {
		return true;
	}


	@Override
	public boolean canFilterSubset() {
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

}
