package org.peakaboo.controller.plotter.calibration;

import java.util.Map;

import org.peakaboo.calibration.BasicDetectorProfile;
import org.peakaboo.calibration.SavedCalibrationSessionV1;
import org.peakaboo.framework.eventful.EventfulBeacon;

public class BasicCalibrationController extends EventfulBeacon implements CalibrationController {

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
