package org.peakaboo.controller.plotter.calibration;

import java.util.Map;

import org.peakaboo.calibration.BasicDetectorProfile;
import org.peakaboo.calibration.SavedCalibrationSession;
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
	public SavedCalibrationSession toSavedV1() {
		return null;
	}

	@Override
	public void loadSavedV1(SavedCalibrationSession saved) {
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
