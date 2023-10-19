package org.peakaboo.controller.plotter.calibration;

import java.io.IOException;
import java.util.Map;

import org.peakaboo.calibration.DetectorProfile;
import org.peakaboo.calibration.SavedCalibrationSession;
import org.peakaboo.framework.eventful.IEventful;

public interface CalibrationController extends IEventful {

	boolean hasDetectorProfile();
	DetectorProfile getDetectorProfile();

	@Deprecated(since="6", forRemoval = true)
	public SavedCalibrationSession toSavedV1();
	@Deprecated(since="6", forRemoval = true)
	public void loadSavedV1(SavedCalibrationSession saved) throws IOException;
	
	Map<String, Object> save();
	void load(Map<String, Object> sessionExtended);
	
}