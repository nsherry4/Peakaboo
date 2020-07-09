package org.peakaboo.controller.settings.store;

public class Settings {

	private static SettingsStore provider;
	
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
