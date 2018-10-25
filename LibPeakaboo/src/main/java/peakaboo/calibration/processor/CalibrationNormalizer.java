package peakaboo.calibration.processor;

import java.util.Map;

import peakaboo.calibration.CalibrationReference;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;

public class CalibrationNormalizer implements CalibrationProcessor {

	@Override
	public void process(CalibrationReference reference, Map<TransitionSeries, Float> calibrations) {
		Float anchorValue = calibrations.get(reference.getAnchor());
		if (anchorValue == null) return;
		for (TransitionSeriesType tst : TransitionSeriesType.values()) {
			normalize(calibrations, tst, anchorValue);
		}
	}

	private void normalize(Map<TransitionSeries, Float> calibrations, TransitionSeriesType tst, float against) {
		
		for (TransitionSeries ts : calibrations.keySet()) {
			if (ts.type != tst) { continue; }
			
			float value = calibrations.get(ts);
			calibrations.put(ts, value/against);			
		}
	}
	
	
}
