package org.peakaboo.framework.druthers.settings;

public interface SettingsStore {
	
	String get(String key, String fallback);
	void set(String key, String value);
	
	default boolean getBoolean(String key, boolean fallback) {
		String value = get(key, null);
		if (value == null) {
			return fallback;
		} else {
			return "True".equals(value);
		}
	}
	
	default void setBoolean(String key, boolean value) {
		set(key, value ? "True" : "False");
	}
	
	default int getInt(String key, int fallback) {
		String value = get(key, null);
		if (value == null) {
			return fallback;
		} else {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				return fallback;
			}
		}
	}
	
	default void setInt(String key, int value) {
		set(key, Integer.toString(value));
	}
	
	
	default float getFloat(String key, float fallback) {
		String value = get(key, null);
		if (value == null) {
			return fallback;
		} else {
			try {
				return Float.parseFloat(value);
			} catch (Exception e) {
				return fallback;
			}
		}
	}
	
	default void setFloat(String key, float value) {
		set(key, Float.toString(value));
	}
	
}
