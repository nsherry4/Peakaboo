package peakaboo.controller.plotter.calibration;

import java.io.File;

import peakaboo.calibration.CalibrationProfile;
import peakaboo.calibration.CalibrationReference;

public class CalibrationModel {

	public CalibrationReference calibrationReference = CalibrationReference.empty();
	
	public CalibrationProfile calibrationProfile = new CalibrationProfile();

	public File calibrationProfileFile;
	
}
