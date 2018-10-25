package peakaboo.calibration.processor;

import java.util.Map;

import peakaboo.calibration.CalibrationReference;
import peakaboo.curvefit.peak.transition.TransitionSeries;

public interface CalibrationProcessor {

	void process(CalibrationReference reference, Map<TransitionSeries, Float> calibrations);

}