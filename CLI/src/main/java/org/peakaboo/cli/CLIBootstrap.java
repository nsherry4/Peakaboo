package org.peakaboo.cli;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.peakaboo.app.Settings;
import org.peakaboo.app.Version;
import org.peakaboo.curvefit.peak.table.PeakTable;
import org.peakaboo.framework.accent.Platform;
import org.peakaboo.framework.accent.log.OneLog;
import org.peakaboo.framework.cyclops.visualization.backend.awt.surfaces.CyclopsSurface;
import org.peakaboo.tier.Tier;

/**
 * The headless equivalent of the Swing app's startup: settings store, plugin
 * registries, and image export surfaces, with no Swing anywhere. Commands call
 * {@link #init} before touching anything in libpeakaboo.
 *
 * @author NAS
 */
public final class CLIBootstrap {

	private static boolean initialized = false;
	private static File configDir;

	private CLIBootstrap() {}

	/** The CLI's own config directory. Only valid after {@link #init}. */
	public static File getConfigDir() {
		return configDir;
	}

	public static synchronized void init(File pluginsDirOverride, boolean verbose) {
		if (initialized) return;
		initialized = true;

		// CLI Settings live separately so it can have its own settings and exclusive
		// access to the settings file -- no clobbering from concurrent writes.
		File settingsDir = Platform.appDirEntry(Version.PROGRAM_NAME, "CLI");
		settingsDir.mkdirs();
		configDir = settingsDir;
		try {
			Settings.load(settingsDir);
		} catch (IOException e) {
			throw new UncheckedIOException("Cannot initialize settings store in " + settingsDir, e);
		}

		OneLog.setAppInfo(Tier.provider().appName() + "-cli", Version.LONG_VERSION);
		OneLog.setVerbose(verbose);
		if (!verbose) {
			// Keep routine chatter off of the terminal. OneLog doesn't expose this level of
			// granularity so we just bump them manually.
			for (Handler handler : OneLog.getRoot().getHandlers()) {
				if (handler instanceof ConsoleHandler) {
					handler.setLevel(Level.SEVERE);
				}
			}
		}

		File pluginsDir = pluginsDirOverride != null
				? pluginsDirOverride
				: Platform.appDirEntry(Version.PROGRAM_NAME, "Plugins");
		Tier.provider().initializePlugins(pluginsDir);

		CyclopsSurface.init();

		System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "" + Settings.getThreadCount());
	}

	/**
	 * Builds the peak table, which takes a few seconds. It's lazy, so only the
	 * commands that deal in fittings need to pay for it.
	 */
	public static void initPeakTable() {
		PeakTable.SYSTEM.getAll();
	}

}
