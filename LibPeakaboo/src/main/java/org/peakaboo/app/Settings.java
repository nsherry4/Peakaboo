package org.peakaboo.app;

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
	
	
	// App settings included here directly (instead of through some Controller)
	// generally require an application restart, as there is no event system to
	// notify the app of changes
	private static final String DISK_BACKED = "org.peakaboo.app.diskbacked";
	public static boolean isDiskstore() {
		return provider.getBoolean(DISK_BACKED, true);
	}
	public static void setDiskstore(boolean diskBacked) {
		provider.setBoolean(DISK_BACKED, diskBacked);
	}
	
	private static final String HEAP_SIZE = "org.peakaboo.app.heapsize";

	private static final String FIRST_RUN = "org.peakaboo.app.firstrun";
	public static boolean isFirstrun() {
		return provider.getBoolean(FIRST_RUN, true);
	}
	public static void setFirstrun(boolean firstrun) {
		provider.setBoolean(FIRST_RUN, firstrun);
	}
	
}
