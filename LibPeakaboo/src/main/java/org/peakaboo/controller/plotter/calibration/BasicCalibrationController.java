package org.peakaboo.controller.plotter.calibration;

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
	public SavedCalibrationSession toSaved() {
		return null;
	}

	@Override
	public void loadSaved(SavedCalibrationSession saved) {
		//Nothing to do
	}

}
