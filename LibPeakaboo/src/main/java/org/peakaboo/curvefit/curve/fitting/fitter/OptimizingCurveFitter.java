package org.peakaboo.curvefit.curve.fitting.fitter;

/**
 * {@link CautiousCurveFitter} at the heavier overfit penalty this fitter has
 * always used.
 *
 * @author NAS
 */
public class OptimizingCurveFitter extends CautiousCurveFitter {

	public OptimizingCurveFitter() {
		overfitPenalty = 5f;
	}

	@Override
	public String pluginName() {
		return "Extra-Cautious Least Squares (formerly 'Optimizing')";
	}

	@Override
	public String toString() {
		return pluginName();
	}

	@Override
	public String pluginDescription() {
		return "Least squares curve fitting with a strong bias against overfitting";
	}

	@Override
	public String pluginVersion() {
		return "2.0";
	}

	@Override
	public String pluginUUID() {
		return "9e7caaf0-4684-4c50-bca7-e6a304a6fd6b";
	}

}
