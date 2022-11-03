package org.peakaboo.curvefit.curve.fitting.fitter;

import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.curve.fitting.ROCurve;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;

public class UnderCurveFitter implements CurveFitter {


	@Override
	public String pluginName() {
		return "Max Under Curve";
	}
	
	@Override
	public String toString() {
		return pluginName();
	}
	

	/**
	 * Fits this curve against spectrum data
	 */
	public FittingResult fit(ReadOnlySpectrum data, ROCurve curve) {
		float scale = this.getRatioForCurveUnderData(data, curve);
		FittingResult result = new FittingResult(curve, scale);
		return result;
	}
	

	/**
	 * Calculates the amount that this fitting should be scaled by to best fit the given data set
	 * 
	 * @param data
	 *            the data to scale the fit to match
	 * @return a scale value
	 */
	private float getRatioForCurveUnderData(ReadOnlySpectrum data, ROCurve curve) {
			
		float maxSignal = maxSignal(data, curve);
		float cutoff;
		
		// calculate cut-off point where we do not consider any signal weaker than this when trying to fit
		if (maxSignal > 0.0) {
			cutoff = (float) Math.log(maxSignal * 2);
			cutoff = cutoff / maxSignal; // expresessed w.r.t strongest signal
		} else {
			cutoff = 0.0f;
		}

		float thisFactor;
		float smallestFactor = Float.MAX_VALUE;
		boolean ratiosConsidered = false;

		
		//look at every point in the ranges covered by transitions
		ReadOnlySpectrum curveSignal = curve.get();
		for (Integer i : curve.getIntenseChannels()) {
			if (i < 0 || i >= data.size()) continue;
			
			float curveChannelSignal = curveSignal.get(i);
			if (curveChannelSignal >= cutoff) {
				thisFactor = data.get(i) / curveChannelSignal;
				if (thisFactor < smallestFactor && !Float.isNaN(thisFactor)) {
					smallestFactor = thisFactor;
					ratiosConsidered = true;
				}
			}
		}

		if (! ratiosConsidered) return 0.0f;

		return smallestFactor;

	}

	@Override
	public String pluginDescription() {
		return "Fits curves by signal's weakest channel, never overfitting";
	}

	@Override
	public boolean pluginEnabled() {
		return true;
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "8aa6a765-dbe8-4d41-8841-8cbae8af6969";
	}

	
}
