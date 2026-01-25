package org.peakaboo.ui.swing.app;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.peakaboo.app.Settings;
import org.peakaboo.framework.accent.log.OneLog;
import org.peakaboo.framework.stratus.api.Stratus;

public class DesktopSettings extends Settings {

	public static void init() {
		//Initialize settings store
		try {
			File settingsDir = DesktopApp.appDir("Settings");
			Settings.load(settingsDir);
		} catch (IOException e) {
			Stratus.removeSplash();
			OneLog.log(Level.SEVERE, "Failed to load persistent settings, Peakaboo must now exit.", e);
			System.exit(2);
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

	
	private static final String DARK_MODE = "org.peakaboo.app.dark-mode";
	public static boolean isDarkMode() {
		return provider.getBoolean(DARK_MODE, false);
	}
	public static void setDarkMode(boolean verbose) {
		provider.setBoolean(DARK_MODE, verbose);
	}
	
	
	private static final String CRASH_AUTOREPORTING = "org.peakaboo.app.crash-autoreport";
	public static boolean isCrashAutoreporting() {
		return provider.getBoolean(CRASH_AUTOREPORTING, false);
	}
	public static void setCrashAutoreporting(boolean verbose) {
		provider.setBoolean(CRASH_AUTOREPORTING, verbose);
	}
	
}
