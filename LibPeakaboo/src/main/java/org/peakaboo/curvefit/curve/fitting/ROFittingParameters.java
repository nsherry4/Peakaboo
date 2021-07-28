package org.peakaboo.curvefit.curve.fitting;

import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.Transition;
import org.peakaboo.curvefit.peak.transition.TransitionShell;

public interface ROFittingParameters {

	ROFittingParameters copy();

	FittingFunction forTransition(Transition transition, TransitionShell type);

	FittingFunction forEscape(Transition transition, Transition escape, Element element, TransitionShell type);

	float getFWHM(Transition t);

	float getFWHMBase();

	EnergyCalibration getCalibration();

	DetectorMaterialType getDetectorMaterial();

	Class<? extends FittingFunction> getFittingFunction();

	boolean getShowEscapePeaks();

}