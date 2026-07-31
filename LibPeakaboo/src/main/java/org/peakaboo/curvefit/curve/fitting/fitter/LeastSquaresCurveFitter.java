package org.peakaboo.curvefit.curve.fitting.fitter;

/**
 * Plain least squares: {@link CautiousCurveFitter} with both of its biases
 * turned off, so overshooting and undershooting count alike and the data is
 * taken as it comes.
 *
 * <p>The clamp has to go as well as the penalty, or else it wouldn't really
 * be least squares
 */
public class LeastSquaresCurveFitter extends CautiousCurveFitter {

	public LeastSquaresCurveFitter() {
		overfitPenalty = 1f;
		clampDataAtZero = false;
	}
	
	@Override
	public String pluginName() {
		return "Plain Least Squares";
	}
	
	@Override
	public String pluginDescription() {
		return "Least squares curve fitting with no bias against overfitting";
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
