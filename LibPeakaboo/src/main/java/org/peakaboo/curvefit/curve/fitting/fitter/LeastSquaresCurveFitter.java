package org.peakaboo.curvefit.curve.fitting.fitter;

public class LeastSquaresCurveFitter extends OptimizingCurveFitter {

	public LeastSquaresCurveFitter() {
		overfitPenalty = 1f;
	}
	
	@Override
	public String name() {
		return "Least-Squares";
	}
	
	@Override
	public String description() {
		return "Curve fitter that minimizes error between curve and signal";
	}
	
}
