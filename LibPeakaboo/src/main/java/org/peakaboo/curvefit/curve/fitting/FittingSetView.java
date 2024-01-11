package org.peakaboo.curvefit.curve.fitting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;

public interface FittingSetView {

	List<ITransitionSeries> getFittedTransitionSeries();
	
	FittingParametersView getFittingParameters();
	
	/**
	 * Returns a new list containing all curves
	 */
	List<CurveView> getCurves();
	
	
	/**
	 * Checks if this fitting set contains any fits
	 * @return true if there are fits, false otherwise
	 */
	default boolean isEmpty() {
		//TODO move this to an abstract class so that we can just get the fitted series directly w/o the copy operation?
		synchronized(this) {
			return getFittedTransitionSeries().isEmpty();
		}
	}
	
	/**
	 * Checks to see if a given {@link ITransitionSeries} is present
	 */
	default boolean hasTransitionSeries(ITransitionSeries ts) {
		//TODO move this to an abstract class so that we can just get the fitted series directly w/o the copy operation?
		synchronized(this) {
			return getFittedTransitionSeries().contains(ts);
		}
	}

	default List<ITransitionSeries> getVisibleTransitionSeries() {
		synchronized(this) {
			List<ITransitionSeries> fittedElements = new ArrayList<>();
			for (ITransitionSeries e : getFittedTransitionSeries()) {
				if (e.isVisible()) fittedElements.add(e);
			}
			return fittedElements;
		}
	}

	
	/**
	 * Returns a new list containing all visible curves
	 */
	default List<CurveView> getVisibleCurves() {
		return getCurves().stream().filter(c -> c.getTransitionSeries().isVisible()).collect(Collectors.toList());
	}


	
	default Optional<CurveView> getCurveForTransitionSeries(ITransitionSeries ts) {
		for (CurveView curve: getCurves()) {
			if (ts.equals(curve.getTransitionSeries())) {
				return Optional.of(curve);
			}
		}
		return Optional.empty();
	}

}