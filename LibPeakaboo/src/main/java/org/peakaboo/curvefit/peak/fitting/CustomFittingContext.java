package org.peakaboo.curvefit.peak.fitting;

import org.peakaboo.curvefit.curve.fitting.FittingParametersView;

public class CustomFittingContext implements FittingContext {

	private float energy, height, fwhm;
	private FittingParametersView parameters;
	
	public CustomFittingContext(FittingParametersView parameters, float energy) {
		this(parameters, energy, 1f);
	}
	
	public CustomFittingContext(FittingParametersView parameters, float energy, float height) {
		this(parameters, energy, height, parameters.getFWHM(energy));
	}
	
	public CustomFittingContext(FittingParametersView parameters, float energy, float height, float fwhm) {
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
	public FittingParametersView getFittingParameters() {
		return parameters;
	}
	
}
