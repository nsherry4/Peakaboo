package peakaboo.calibration;

import java.util.Map;

import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;

public interface CalibrationProcessor {

	void process(CalibrationReference reference, Map<LegacyTransitionSeries, Float> calibrations);
	
	default void process(CalibrationReference reference, CalibrationProfile profile) {
		process(reference, profile.calibrations);
	}

}