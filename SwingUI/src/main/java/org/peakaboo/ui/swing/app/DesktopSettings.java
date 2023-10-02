package org.peakaboo.ui.swing.app;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.app.Settings;
import org.peakaboo.framework.druthers.serialize.YamlSerializer;
import org.peakaboo.framework.stratus.api.Stratus;

public class DesktopSettings extends Settings {

	public static void init() {
		//Initialize settings store
		try {
			File settingsDir = DesktopApp.appDir("Settings");
			boolean firstSettings = !settingsDir.exists();
			Settings.load(settingsDir);
			if (firstSettings) {
				//This is the first time running a version of Peakaboo that uses the new
				//Druthers settings store. We'll try to load existing settings into it
				transferSettings();
			}
						
		} catch (IOException e) {
			Stratus.removeSplash();
			PeakabooLog.get().log(Level.SEVERE, "Failed to load persistent settings, Peakaboo must now exit.", e);
			System.exit(2);
		}	
	}
	
	//TODO: Remove this in Peakaboo 6
	/**
	 * This method exists to transfer settings from the old
	 * method of storing them to the new one
	 */
	private static void transferSettings() {
		File oldFile = new File(DesktopApp.appDir() + "/settings.yaml");
		if (!oldFile.exists()) return;
		try {
			Map<String, Map<String, Boolean>> oldSettings = YamlSerializer.deserializeGeneric(oldFile);
			Map<String, Boolean> oldPersistent = oldSettings.get("persistent");
			Settings.provider().setBoolean("org.peakaboo.controller.plot.view.constantscale", 	oldPersistent.get("consistentScale"));
			Settings.provider().setBoolean("org.peakaboo.controller.plot.view.monochrome", 		oldPersistent.get("monochrome"));
			Settings.provider().setBoolean("org.peakaboo.controller.plot.view.fit.intensity", 	oldPersistent.get("showElementFitIntensities"));
			Settings.provider().setBoolean("org.peakaboo.controller.plotter.view.fit.markers", 	oldPersistent.get("showElementFitMarkers"));
			Settings.provider().setBoolean("org.peakaboo.controller.plot.view.fit.individual", 	oldPersistent.get("showIndividualFittings"));
		} catch (IOException e) {
			PeakabooLog.get().warning("Failed to transfer old settings");
		}
	}
	
	private static final String FIRST_RUN = "org.peakaboo.app.firstrun";
	public static boolean isFirstrun() {
		return provider.getBoolean(FIRST_RUN, true);
	}
	public static void setFirstrun(boolean firstrun) {
		provider.setBoolean(FIRST_RUN, firstrun);
	}
	
	
	private static final String ACCENT_COLOUR = "org.peakaboo.app.accent-colour";
	public static String getAccentColour() {
		return provider.get(ACCENT_COLOUR, "Blue");
	}
	public static void setAccentColour(String colour) {
		provider.set(ACCENT_COLOUR, colour);
	}

	
	private static final String CRASH_AUTOREPORTING = "org.peakaboo.app.crash-autoreport";
	public static boolean isCrashAutoreporting() {
		return provider.getBoolean(CRASH_AUTOREPORTING, false);
	}
	public static void setCrashAutoreporting(boolean verbose) {
		provider.setBoolean(CRASH_AUTOREPORTING, verbose);
	}
	
}
