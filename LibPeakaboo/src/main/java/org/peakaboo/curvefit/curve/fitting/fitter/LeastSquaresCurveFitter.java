package org.peakaboo.curvefit.curve.fitting.fitter;

public class LeastSquaresCurveFitter extends CautiousCurveFitter {

	public LeastSquaresCurveFitter() {
		overfitPenalty = 1f;
	}
	
	@Override
	public String pluginName() {
		return "Least-Squares";
	}
	
	@Override
	public String pluginDescription() {
		return "Least squares curve fitting with no guard against overfitting";
	}
	
	@Override
	public boolean pluginEnabled() {
		return true;
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}
	
	@Override
	public String pluginUUID() {
		return "b7599343-ce5e-4438-b829-909500f8fbd3";		
	}
	
}
