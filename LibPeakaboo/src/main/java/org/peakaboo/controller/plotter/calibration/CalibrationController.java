package org.peakaboo.controller.plotter.calibration;

import java.io.IOException;
import java.util.Map;

import org.peakaboo.calibration.DetectorProfile;
import org.peakaboo.calibration.SavedCalibrationSessionV1;
import org.peakaboo.framework.eventful.IEventfulBeacon;

public interface CalibrationController extends IEventfulBeacon {

	boolean hasDetectorProfile();
	DetectorProfile getDetectorProfile();

	@Deprecated(since="6", forRemoval = true)
	public SavedCalibrationSessionV1 toSavedV1();
	@Deprecated(since="6", forRemoval = true)
	public void loadSavedV1(SavedCalibrationSessionV1 saved) throws IOException;
	
	Map<String, Object> save();
	void load(Map<String, Object> sessionExtended);
	
}