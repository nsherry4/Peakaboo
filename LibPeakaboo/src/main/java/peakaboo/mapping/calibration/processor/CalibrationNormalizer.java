package peakaboo.mapping.calibration.processor;

import java.util.Map;

import peakaboo.curvefit.peak.transition.TransitionSeries;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;
import peakaboo.mapping.calibration.CalibrationReference;

public class CalibrationNormalizer implements CalibrationProcessor {

	@Override
	public void process(CalibrationReference reference, Map<TransitionSeries, Float> calibrations) {
		Float anchorValue = calibrations.get(reference.getAnchor());
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
