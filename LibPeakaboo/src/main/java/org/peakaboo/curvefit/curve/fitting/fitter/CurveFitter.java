package org.peakaboo.curvefit.curve.fitting.fitter;

import org.peakaboo.curvefit.curve.fitting.Curve;
import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.curve.fitting.ROCurve;
import org.peakaboo.curvefit.curve.fitting.solver.GreedyFittingSolver;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

/**
 * Defines a method by which a single {@link Curve} is fitted against a
 * {@link Spectrum} to determine how much the curve should be scaled by to match
 * the data. This is used with the {@link GreedyFittingSolver}, which allows each
 * curve in order to fit against as much of the remaining signal as it can. 
 * implementations of this interface provide different algorithms to determine
 * how much can be fit.
 * 
 * @author NAS
 *
 */
public interface CurveFitter extends BoltJavaPlugin {

	FittingResult fit(ReadOnlySpectrum data, ROCurve curve);
		
	default float maxSignal(ReadOnlySpectrum data, ROCurve curve) {
		
		float maxSignal = Float.MIN_VALUE;
		boolean hasSignal = false;
		float currentSignal;
		
		//look at every point in the ranges covered by transitions, find the max intensity
		for (Integer i : curve.getIntenseChannels()) {
			if (i < 0 || i >= data.size()) continue;
			currentSignal = data.get(i);
			if (currentSignal > maxSignal) maxSignal = currentSignal;
			hasSignal = true;
		}
		if (! hasSignal) return 0.0f;
		return maxSignal;
	}
	
}
