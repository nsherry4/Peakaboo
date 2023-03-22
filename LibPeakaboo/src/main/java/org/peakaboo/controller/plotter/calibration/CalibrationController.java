package org.peakaboo.controller.plotter.calibration;

import org.peakaboo.calibration.DetectorProfile;
import org.peakaboo.calibration.SavedCalibrationSession;
import org.peakaboo.framework.eventful.IEventful;

public interface CalibrationController extends IEventful {

	boolean hasDetectorProfile();
	DetectorProfile getDetectorProfile();

	public SavedCalibrationSession toSaved();
	public void loadSaved(SavedCalibrationSession saved);
	
}