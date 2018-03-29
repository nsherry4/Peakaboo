package peakaboo.curvefit.model.fitting.functions;

import peakaboo.curvefit.model.transition.Transition;

public class FittingFunctionFactory {

	public static FittingFunction get(Transition t) {
		return get(t.energyValue, t.getFWHM(), t.relativeIntensity);
	}
	
	public static FittingFunction get(float mean, float fwhm, float height) {
		return pseudovoigt(mean, fwhm, height);
	}
	
	public static FittingFunction gauss(float mean, float fwhm, float height) {
		return new GaussianFittingFunction(mean, fwhm, height);
	}
	
	public static FittingFunction lorentz(float mean, float fwhm, float height) {
		return new LorentzFittingFunction(mean, fwhm, height);
	}
	
	public static FittingFunction ida(float mean, float fwhm, float height) {
		return new IdaFittingFunction(mean, fwhm, height);
	}
	
	//Fast approximation of a voigt function by summing 70% gaussian, 30% lorentzian
	public static FittingFunction pseudovoigt(float mean, float width, float height) {
		return new MixedFittingFunction(gauss(mean, width, height), lorentz(mean, width, height), 0.8f);
	}
	
}
