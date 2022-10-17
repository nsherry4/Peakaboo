package org.peakaboo.controller.plotter.calibration;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.calibration.SavedCalibrationSession;
import org.peakaboo.framework.eventful.IEventful;

public interface CalibrationController extends IEventful {

	boolean hasCalibrationProfile();
	CalibrationProfile getCalibrationProfile();

	public SavedCalibrationSession toSaved();
	public void loadSaved(SavedCalibrationSession saved);
	
}