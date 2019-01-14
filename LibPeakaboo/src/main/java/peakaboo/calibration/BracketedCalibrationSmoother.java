package peakaboo.calibration;

import java.util.Map;

import peakaboo.curvefit.peak.transition.ITransitionSeries;

public class BracketedCalibrationSmoother implements CalibrationProcessor {

	private float percentRange;
	public BracketedCalibrationSmoother(float percentRange) {
		this.percentRange = percentRange;
	}
	
	@Override
	public void process(CalibrationProfile profile) {
		
		AggressiveCalibrationSmoother smoother = new AggressiveCalibrationSmoother(1);
		CalibrationProfile copy = new CalibrationProfile(profile);
		smoother.process(copy);
		
		for (ITransitionSeries ts : profile.calibrations.keySet()) {
			float original = profile.getCalibration(ts);
			float modified = copy.getCalibration(ts);
			
			float min = Math.max(modified, original*(1f-percentRange));
			float value = Math.min(min, original*(1f+percentRange));
			
			profile.getCalibrations().put(ts, value);
		}
		
	}

	@Override
	public void process(CalibrationReference reference, Map<ITransitionSeries, Float> calibrations) {
		//Not Used
	}
	
}
