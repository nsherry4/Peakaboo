package org.peakaboo.controller.plotter.calibration;

import java.util.Map;

import org.peakaboo.calibration.BasicDetectorProfile;
import org.peakaboo.calibration.SavedCalibrationSessionV1;
import org.peakaboo.framework.eventful.Eventful;

public class BasicCalibrationController extends Eventful implements CalibrationController {

	private BasicDetectorProfile profile = new BasicDetectorProfile();
	
	@Override
	public boolean hasDetectorProfile() {
		return false;
	}

	@Override
	public BasicDetectorProfile getDetectorProfile() {
		return profile;
	}

	@Override
	public SavedCalibrationSessionV1 toSavedV1() {
		return null;
	}

	@Override
	public void loadSavedV1(SavedCalibrationSessionV1 saved) {
		//Nothing to do
	}

	@Override
	public Map<String, Object> save() {
		return Map.of();
	}

	@Override
	public void load(Map<String, Object> sessionExtended) {
		// NOOP
	}

}
