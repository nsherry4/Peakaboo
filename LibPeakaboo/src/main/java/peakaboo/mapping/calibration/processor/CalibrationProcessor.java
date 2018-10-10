package peakaboo.mapping.calibration.processor;

import java.util.Map;

import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.mapping.calibration.CalibrationReference;

public interface CalibrationProcessor {

	void process(CalibrationReference reference, Map<TransitionSeries, Float> calibrations);

}