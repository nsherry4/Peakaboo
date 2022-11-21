package org.peakaboo.curvefit.peak.fitting;

import org.peakaboo.curvefit.curve.fitting.ROFittingParameters;

public class DelegatingFittingContext implements FittingContext {

	private FittingContext backer;
	
	public DelegatingFittingContext(FittingContext backer) {
		this.backer = backer;
	}
	
	@Override
	public float getHeight() {
		return backer.getHeight();
	}

	@Override
	public float getEnergy() {
		return backer.getEnergy();
	}

	@Override
	public float getFWHM() {
		return backer.getFWHM();
	}

	@Override
	public ROFittingParameters getFittingParameters() {
		return backer.getFittingParameters();
	}

}
