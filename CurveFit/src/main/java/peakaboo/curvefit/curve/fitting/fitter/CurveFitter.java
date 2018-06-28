package peakaboo.curvefit.curve.fitting.fitter;

import peakaboo.curvefit.curve.fitting.Curve;
import peakaboo.curvefit.curve.fitting.FittingResult;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;

/**
 * Defines a method by which a {@link Curve} is fitted against a
 * {@link Spectrum} to determine how much the curve should be scaled by to match
 * the data.
 * 
 * @author NAS
 *
 */
public interface CurveFitter {

	public FittingResult fit(ReadOnlySpectrum data, Curve curve);
	
}
