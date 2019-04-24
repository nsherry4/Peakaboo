package org.peakaboo.controller.plotter.calibration;

import java.io.File;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.calibration.CalibrationReference;

public class CalibrationModel {

	public CalibrationReference calibrationReference = CalibrationReference.empty();
	
	public CalibrationProfile calibrationProfile = new CalibrationProfile();

	public File calibrationProfileFile;
	
}
