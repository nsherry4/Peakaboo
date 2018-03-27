package peakaboo.curvefit.model.fittingfunctions;

public class FittingFunctionFactory {

	public static FittingFunction get(float mean, float width, float height) {
		return new GaussianFittingFunction(mean, width, height);
	}
	
}
