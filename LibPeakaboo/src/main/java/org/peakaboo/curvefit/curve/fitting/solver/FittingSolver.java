package org.peakaboo.curvefit.curve.fitting.solver;

import org.peakaboo.curvefit.curve.fitting.Curve;
import org.peakaboo.curvefit.curve.fitting.FittingResultSetView;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.curve.fitting.FittingSetView;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

/**
 * Defines a method by which a {@link FittingSet} of {@link Curve}s are fit to a given {@link Spectrum}
 * @author NAS
 *
 */
public interface FittingSolver extends BoltJavaPlugin {

	public static record FittingSolverContext (ReadOnlySpectrum data, FittingSetView fittings, CurveFitter fitter) {};
	
	FittingResultSetView solve(FittingSolverContext ctx);
	
}
