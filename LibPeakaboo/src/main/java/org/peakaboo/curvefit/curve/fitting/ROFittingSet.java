package org.peakaboo.curvefit.curve.fitting;

import java.util.List;
import java.util.Optional;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;

public interface ROFittingSet {

	boolean isEmpty();

	boolean hasTransitionSeries(ITransitionSeries ts);

	List<ITransitionSeries> getFittedTransitionSeries();

	List<ITransitionSeries> getVisibleTransitionSeries();

	ROFittingParameters getFittingParameters();

	/**
	 * Returns a new list containing all visible curves
	 */
	List<ROCurve> getVisibleCurves();

	/**
	 * Returns a new list containing all curves
	 */
	List<ROCurve> getCurves();
	
	Optional<ROCurve> getCurveForTransitionSeries(ITransitionSeries ts);

}