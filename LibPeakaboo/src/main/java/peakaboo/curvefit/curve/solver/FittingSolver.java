package peakaboo.curvefit.curve.solver;

import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import peakaboo.curvefit.curve.Curve;
import peakaboo.curvefit.curve.FittingResultSet;
import peakaboo.curvefit.curve.FittingSet;
import peakaboo.curvefit.curve.fitter.CurveFitter;

/**
 * Defines a method by which a {@link FittingSet} of {@link Curve}s are fit to a given {@link Spectrum}
 * @author NAS
 *
 */
public interface FittingSolver {

	String name();
	
	FittingResultSet solve(ReadOnlySpectrum data, FittingSet fittings, CurveFitter fitter);
	
}
