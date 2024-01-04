package org.peakaboo.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

import org.peakaboo.framework.druthers.settings.DummySettingsStore;
import org.peakaboo.framework.druthers.settings.SettingsStore;
import org.peakaboo.framework.druthers.settings.YamlSettingsStore;
import org.peakaboo.framework.plural.Plural;

public class Settings {

	protected static SettingsStore provider = new DummySettingsStore();
	
	public static void load(File directory) throws IOException {
		  provider = new YamlSettingsStore(directory);
	}
	
	public static void load(SettingsStore impl) {
		  provider = impl;
	}
	
	public static SettingsStore provider() {
		if (provider == null || provider instanceof DummySettingsStore) {
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
	
	
	private static final String VERBOSE_LOGGING = "org.peakaboo.app.verbose-logging";
	public static boolean isVerboseLogging() {
		return provider.getBoolean(VERBOSE_LOGGING, false);
	}
	public static void setVerboseLogging(boolean verbose) {
		provider.setBoolean(VERBOSE_LOGGING, verbose);
		PeakabooLog.reloadSettings();
	}
	
	
	
	private static final String THREAD_COUNT = "org.peakaboo.app.threadcount";
	public static int getThreadCount() {
		return provider.getInt(THREAD_COUNT, Plural.cores());
	}
	public static void setThreadCount(int threadcount) {
		provider.setInt(THREAD_COUNT, threadcount);
	}
	

	private static final String HEAP_SIZE_MB = "org.peakaboo.app.heapsize-megabytes";
	public static int getHeapSizeMegabytes() {
		return provider.getInt(HEAP_SIZE_MB, Math.min(Math.max(128, (int)(Env.maxHeap() * 0.75f)), 2048)  );
	}
	public static void setHeapSizeMegabytes(int size) {
		if (size < 128) { size = 128; }
		if (size > Env.maxHeap()) { size = (int) Env.maxHeap(); }
		provider.setInt(HEAP_SIZE_MB, size);
		writeHeapConfig();
	}
	
	
	private static final String HEAP_SIZE_PERCENT = "org.peakaboo.app.heapsize-percent";
	public static int getHeapSizePercent() {
		return provider.getInt(HEAP_SIZE_PERCENT, 75);
	}
	public static void setHeapSizePercent(int size) {
		if (size > 95) { size = 95; }
		if (size < 2) { size = 2; }
		provider.setInt(HEAP_SIZE_PERCENT, size);
		writeHeapConfig();
	}
	
	
	private static final String HEAP_SIZE_IS_PERCENT = "org.peakaboo.app.heapsize-is-percent";
	public static boolean isHeapSizePercent() {
		return provider.getBoolean(HEAP_SIZE_IS_PERCENT, true);
	}
	public static void setHeapSizeIsPercent(boolean isPercent) {
		provider.setBoolean(HEAP_SIZE_IS_PERCENT, isPercent);
		writeHeapConfig();
	}

	
	private static void writeHeapConfig() {
		int size;
		String jvmOption;
		
		if (isHeapSizePercent()) {
			size = getHeapSizePercent();
			if (size < 2 || size > 95) {
				throw new IllegalArgumentException("Invalid heap size");
			}
			jvmOption = "java-options=-XX:MaxRAMPercentage=" + size;
		} else {
			size = getHeapSizeMegabytes();
			if (size < 128 || size > Env.maxHeap()) {
				throw new IllegalArgumentException("Invalid heap size");
			}
			jvmOption = "java-options=-Xmx" + size + "m";
		}

		//Load the system-wide cfg file for the jpackage launcher
		try {
			File sourceCFG = Env.systemCFGFile(Version.program_name);
			String cfgContents = Files.readString(sourceCFG.toPath());
			//replace the default memory option with the new one
			cfgContents = cfgContents.replace("java-options=-XX:MaxRAMPercentage=75", jvmOption);
			File userCFG = Env.userCFGFile(Version.program_name);
			Files.writeString(userCFG.toPath(), cfgContents);
		} catch (IOException e) {
			PeakabooLog.get().log(Level.WARNING, "Cannot write to per-user Peakaboo.cfg file", e);
			throw new RuntimeException(e);
		}
		
		

		
	}

	
}
