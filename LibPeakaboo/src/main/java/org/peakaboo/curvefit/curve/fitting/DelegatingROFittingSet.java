package org.peakaboo.curvefit.curve.fitting;

import java.util.List;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;

public class DelegatingROFittingSet implements ROFittingSet {

	private ROFittingSet backer;
	
	public DelegatingROFittingSet(ROFittingSet backer) {
		this.backer = backer;
	}

	@Override
	public boolean isEmpty() {
		return backer.isEmpty();
	}

	@Override
	public boolean hasTransitionSeries(ITransitionSeries ts) {
		return backer.hasTransitionSeries(ts);
	}

	@Override
	public List<ITransitionSeries> getFittedTransitionSeries() {
		return backer.getFittedTransitionSeries();
	}

	@Override
	public List<ITransitionSeries> getVisibleTransitionSeries() {
		return backer.getVisibleTransitionSeries();
	}

	@Override
	public ROFittingParameters getFittingParameters() {
		return new DelegatingROFittingParameters(backer.getFittingParameters());
	}

	@Override
	public List<Curve> getVisibleCurves() {
		return backer.getVisibleCurves();
	}

	@Override
	public List<Curve> getCurves() {
		return backer.getCurves();
	}
	
	
	public String toString() {
		return backer.toString();
	}


	
	
}
