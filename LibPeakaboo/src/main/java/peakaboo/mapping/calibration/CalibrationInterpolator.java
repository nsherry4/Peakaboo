package peakaboo.mapping.calibration;

import java.util.Map;

import peakaboo.curvefit.peak.transition.TransitionSeries;

public interface CalibrationInterpolator {

	void interpolate(Map<TransitionSeries, Float> calibrations);

}