package org.peakaboo.curvefit.curve.fitting;

import java.util.List;
import java.util.Optional;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;

public class DelegatingFittingSetView implements FittingSetView {

	private FittingSetView backer;
	
	public DelegatingFittingSetView(FittingSetView backer) {
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
	public FittingParametersView getFittingParameters() {
		return new DelegatingFittingParametersView(backer.getFittingParameters());
	}

	@Override
	public List<CurveView> getVisibleCurves() {
		return backer.getVisibleCurves();
	}

	@Override
	public List<CurveView> getCurves() {
		return backer.getCurves();
	}
	
	
	public String toString() {
		return backer.toString();
	}

	public Optional<CurveView> getCurveForTransitionSeries(ITransitionSeries ts) {
		return backer.getCurveForTransitionSeries(ts);
	}


	
	
}
