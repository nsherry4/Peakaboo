package org.peakaboo.framework.druthers.settings;

public class DummySettingsStore implements SettingsStore {

	@Override
	public String get(String key, String fallback) {
		return fallback;
	}

	@Override
	public void set(String key, String value) {
		// NOOP
	}

}
