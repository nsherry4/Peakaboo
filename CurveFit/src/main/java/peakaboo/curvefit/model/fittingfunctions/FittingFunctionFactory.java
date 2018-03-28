package peakaboo.curvefit.model.fittingfunctions;

public class FittingFunctionFactory {

	public static FittingFunction get(float mean, float width, float height) {
		return pseudovoigt(mean, width, height);
	}
	
	public static FittingFunction gauss(float mean, float sigma, float height) {
		return new GaussianFittingFunction(mean, sigma, height);
	}
	
	public static FittingFunction lorentz(float mean, float gamma, float height) {
		return new LorentzFittingFunction(mean, gamma, height);
	}
	
	public static FittingFunction ida(float mean, float gamma, float height) {
		return new IdaFittingFunction(mean, gamma, height);
	}
	
	//Fast approximation of a voigt function by summing 70% gaussian, 30% lorentzian
	public static FittingFunction pseudovoigt(float mean, float width, float height) {
		return new MixedFittingFunction(gauss(mean, width, height), lorentz(mean, width, height), 0.7f);
	}
	
}
