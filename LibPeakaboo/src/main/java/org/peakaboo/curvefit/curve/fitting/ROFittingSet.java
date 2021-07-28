package org.peakaboo.curvefit.curve.fitting;

import java.util.List;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;

public interface ROFittingSet {

	boolean isEmpty();

	boolean hasTransitionSeries(ITransitionSeries ts);

	List<ITransitionSeries> getFittedTransitionSeries();

	List<ITransitionSeries> getVisibleTransitionSeries();

	ROFittingParameters getFittingParameters();

	List<Curve> getVisibleCurves();

	List<Curve> getCurves();

}