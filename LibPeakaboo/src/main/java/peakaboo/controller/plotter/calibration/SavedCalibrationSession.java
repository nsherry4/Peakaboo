package peakaboo.controller.plotter.calibration;

import java.io.File;

import peakaboo.calibration.CalibrationPluginManager;
import peakaboo.calibration.CalibrationProfile;
import peakaboo.calibration.CalibrationReference;

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

	public SavedCalibrationSession loadInto(CalibrationController controller) {
		
		if (profileYaml != null) {
			controller.setCalibrationProfile(CalibrationProfile.load(profileYaml), new File(profileFilename));
		}
		
		if (referenceUUID != null) {
			CalibrationReference reference;
			if (CalibrationPluginManager.SYSTEM.getPlugins().hasUUID(referenceUUID)) {
				reference = CalibrationPluginManager.SYSTEM.getPlugins().getByUUID(referenceUUID).create();
			} else {
				reference = CalibrationReference.load(referenceYaml);
			}
			controller.loadCalibrationReference(reference);
		}
		return this;
	}
	
}
