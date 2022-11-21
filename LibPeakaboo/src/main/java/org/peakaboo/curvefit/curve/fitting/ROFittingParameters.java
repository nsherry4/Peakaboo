package org.peakaboo.curvefit.curve.fitting;

import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.Transition;
import org.peakaboo.curvefit.peak.transition.TransitionShell;

public interface ROFittingParameters {

	ROFittingParameters copy();

	FittingFunction forTransition(Transition transition);

	FittingFunction forEscape(Transition transition, Transition escape, Element element);

	/**
	 * The FWHM value for a {@link Transition} changes based on the energy level. This method 
	 * calculates the FWHM value which should be used for this energy level.
	 */
	float getFWHM(float energy);
	
	/**
	 * The FWHM value for a {@link Transition} changes based on the energy level. This method 
	 * calculates the FWHM value which should be used for this energy level.
	 */
	default float getFWHM(Transition t) {
		return getFWHM(t.energyValue);
	}
	
	float getFWHMBase();

	EnergyCalibration getCalibration();

	DetectorMaterialType getDetectorMaterial();

	Class<? extends FittingFunction> getFittingFunction();

	boolean getShowEscapePeaks();

	

}