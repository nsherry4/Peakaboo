package org.peakaboo.controller.plotter.calibration;

import java.io.File;

import org.peakaboo.calibration.CalibrationPluginManager;
import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.calibration.CalibrationReference;

public class SavedCalibrationSession {

	public String profileYaml;
	public String profileFilename;
	public String referenceUUID;
	public String referenceYaml;
	
	public SavedCalibrationSession storeFrom(CalibrationController controller) {
		
		if (controller.hasCalibrationProfile()) {
			profileYaml = CalibrationProfile.save(controller.getCalibrationProfile());
			File file = controller.getCalibrationProfileFile();
			if (file != null) {
				profileFilename = controller.getCalibrationProfileFile().toString();
			} else {
				profileFilename = null;
			}
		}

		
		if (controller.hasCalibrationReference()) {
			referenceUUID = controller.getCalibrationReference().getUuid();
			referenceYaml = CalibrationReference.save(controller.getCalibrationReference());
		}
		
		return this;
	}

	public void loadInto(CalibrationController controller) {
		
		if (profileYaml != null) {
			controller.setCalibrationProfile(CalibrationProfile.load(profileYaml), new File(profileFilename));
		} else {
			controller.setCalibrationProfile(new CalibrationProfile(), null);
		}
		
		if (referenceUUID != null) {
			CalibrationReference reference;
			if (CalibrationPluginManager.SYSTEM.getPlugins().hasUUID(referenceUUID)) {
				reference = CalibrationPluginManager.SYSTEM.getPlugins().getByUUID(referenceUUID).create();
			} else {
				reference = CalibrationReference.load(referenceYaml);
			}
			controller.setCalibrationReference(reference);
		} else {
			controller.setCalibrationReference(CalibrationReference.empty());
		}
	}
	
}
