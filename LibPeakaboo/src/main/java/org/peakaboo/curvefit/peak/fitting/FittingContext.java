package org.peakaboo.curvefit.peak.fitting;

import org.peakaboo.curvefit.curve.fitting.FittingParametersView;

public interface FittingContext {

	float getHeight();

	float getEnergy();

	float getFWHM();
	
	FittingParametersView getFittingParameters();
	
}