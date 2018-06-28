package peakaboo.curvefit.curve.fitting.solver;

import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.curvefit.curve.fitting.FittingSet;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import scitypes.ReadOnlySpectrum;

public interface FittingSolver {

	FittingResultSet solve(ReadOnlySpectrum data, FittingSet fittings, CurveFitter fitter);
	
}
