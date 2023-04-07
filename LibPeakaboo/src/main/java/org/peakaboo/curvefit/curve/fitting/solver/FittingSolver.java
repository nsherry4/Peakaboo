package org.peakaboo.curvefit.curve.fitting.solver;

import org.peakaboo.curvefit.curve.fitting.Curve;
import org.peakaboo.curvefit.curve.fitting.FittingResultSet;
import org.peakaboo.curvefit.curve.fitting.FittingSet;
import org.peakaboo.curvefit.curve.fitting.ROFittingSet;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterPlugin;
import org.peakaboo.framework.autodialog.model.SelfDescribing;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

/**
 * Defines a method by which a {@link FittingSet} of {@link Curve}s are fit to a given {@link Spectrum}
 * @author NAS
 *
 */
public interface FittingSolver extends SelfDescribing {

	FittingResultSet solve(ReadOnlySpectrum data, ROFittingSet fittings, CurveFitterPlugin fitter);
	
}
