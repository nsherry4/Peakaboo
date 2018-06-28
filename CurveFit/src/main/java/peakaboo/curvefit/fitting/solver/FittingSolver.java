package peakaboo.curvefit.fitting.solver;

import peakaboo.curvefit.fitting.FittingResultSet;
import peakaboo.curvefit.fitting.FittingSet;
import scitypes.ReadOnlySpectrum;

public interface FittingSolver {

	FittingResultSet solve(ReadOnlySpectrum data, FittingSet fittings);
	
}
