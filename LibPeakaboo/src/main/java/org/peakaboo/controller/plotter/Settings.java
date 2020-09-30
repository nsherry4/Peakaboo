package org.peakaboo.controller.plotter;

import java.io.File;
import java.io.IOException;

import org.peakaboo.framework.druthers.settings.SettingsStore;
import org.peakaboo.framework.druthers.settings.YamlSettingsStore;

public class Settings {

	private static SettingsStore provider;

	public static void init(File directory) throws IOException {
		  provider = new YamlSettingsStore(directory);
	}
	
	public static void init(SettingsStore impl) {
		  provider = impl;
	}
	
	public static SettingsStore provider() {
		if (provider == null) {
			throw new RuntimeException("Settings provider has not been initialized");
		}
		return provider;
	}
	
}
