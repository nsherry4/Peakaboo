package org.peakaboo.controller.plotter.calibration;

import org.peakaboo.calibration.BasicCalibrationProfile;
import org.peakaboo.calibration.SavedCalibrationSession;
import org.peakaboo.framework.eventful.Eventful;

public class BasicCalibrationController extends Eventful implements CalibrationController {

	private BasicCalibrationProfile profile = new BasicCalibrationProfile();
	
	@Override
	public boolean hasCalibrationProfile() {
		return false;
	}

	@Override
	public BasicCalibrationProfile getCalibrationProfile() {
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
