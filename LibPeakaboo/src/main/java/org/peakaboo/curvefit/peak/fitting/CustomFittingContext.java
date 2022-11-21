package org.peakaboo.curvefit.peak.fitting;

import org.peakaboo.curvefit.curve.fitting.ROFittingParameters;

public class CustomFittingContext implements FittingContext {

	private float energy, height, fwhm;
	private ROFittingParameters parameters;
	
	public CustomFittingContext(ROFittingParameters parameters, float energy) {
		this(parameters, energy, 1f);
	}
	
	public CustomFittingContext(ROFittingParameters parameters, float energy, float height) {
		this(parameters, energy, height, parameters.getFWHM(energy));
	}
	
	public CustomFittingContext(ROFittingParameters parameters, float energy, float height, float fwhm) {
		this.energy = energy;
		this.height = height;
		this.fwhm = fwhm;
		this.parameters = parameters;
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public float getEnergy() {
		return energy;
	}

	@Override
	public float getFWHM() {
		return fwhm;
	}

	@Override
	public ROFittingParameters getFittingParameters() {
		return parameters;
	}
	
}
