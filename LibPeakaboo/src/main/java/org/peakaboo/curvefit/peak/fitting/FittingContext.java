package org.peakaboo.curvefit.peak.fitting;

import org.peakaboo.curvefit.curve.fitting.ROFittingParameters;

public interface FittingContext {

	float getHeight();

	float getEnergy();

	float getFWHM();
	
	ROFittingParameters getFittingParameters();
	
}