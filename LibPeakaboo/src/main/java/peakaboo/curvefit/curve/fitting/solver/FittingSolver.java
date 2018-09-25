package peakaboo.curvefit.curve.fitting.solver;

import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import peakaboo.curvefit.curve.fitting.Curve;
import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.curvefit.curve.fitting.FittingSet;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;

/**
 * Defines a method by which a {@link FittingSet} of {@link Curve}s are fit to a given {@link Spectrum}
 * @author NAS
 *
 */
public interface FittingSolver {

	String name();
	
	FittingResultSet solve(ReadOnlySpectrum data, FittingSet fittings, CurveFitter fitter);
	
}
