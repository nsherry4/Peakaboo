package org.peakaboo.calibration;

import java.util.Map;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;

public interface CalibrationProcessor {


	void process(CalibrationReference reference, Map<ITransitionSeries, Float> calibrations);
	
	default void process(CalibrationProfile profile) {
		process(profile.getReference(), profile.calibrations);
	}
	


}