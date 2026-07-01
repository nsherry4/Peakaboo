package org.peakaboo.dataset.source.plugin.plugins.universalhdf5;

import java.io.IOException;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooConfiguration;
import org.peakaboo.dataset.io.DataInputAdapter;
import org.peakaboo.framework.accent.log.OneLog;

/**
 * Opens {@link HDFReader}s using the configured backend. AUTO (the default) prefers the
 * native library where it loads with the pure-Java jhdf library as a fallback. This lets
 * the universalhdf5 DataSource work even where the native binary is unavailable (eg
 * Android, non-x86/Apple). The backend can be pinned {@link PeakabooConfiguration#hdfBackend}.
 *
 * @author NAS
 */
public class HDFReaders {

	/** Backend preference enum */
	public enum Backend { AUTO, NATIVE, JHDF }

	private static volatile Boolean nativeAvailable;

	private HDFReaders() {}

	/** Opens the given file with the configured backend. */
	public static HDFReader open(DataInputAdapter path) throws IOException {
		return useNative() ? new NativeHDF5Reader(path) : new JHDFReader(path);
	}

	/**
	 * Resolves and caches the backend choice ahead of time. The native probe has to extract
	 * and load a multi-MB native library, which otherwise stalls the first HDFReader opened
	 * (including during format detection, which probes every plugin). Callers can run this off
	 * the critical path (eg a startup thread) so the result is ready by first use. This only
	 * probes when the configured backend could actually use the native library.
	 */
	public static void warmUp() {
		if (configuredBackend() != Backend.JHDF) {
			nativeAvailable();
		}
	}

	private static boolean useNative() {
		switch (configuredBackend()) {
			case NATIVE: return true;
			case JHDF:   return false;
			case AUTO:
			default:     return nativeAvailable();
		}
	}

	private static Backend configuredBackend() {
		try {
			return Backend.valueOf(PeakabooConfiguration.hdfBackend.trim().toUpperCase());
		} catch (RuntimeException e) {
			//an unrecognised value is treated as AUTO rather than failing the read
			return Backend.AUTO;
		}
	}

	/**
	 * Whether the native HDF5 backend can be used on this platform. Check if the native backend
	 * loads and cache the result once known.
	 */
	public static synchronized boolean nativeAvailable() {
		if (nativeAvailable == null) {
			try {
				NativeHDF5Reader.probeNativeLibrary();
				nativeAvailable = true;
				OneLog.log(Level.INFO, "Native HDF5 backend available");
			} catch (Throwable t) {
				//catch Throwable: a missing native lib surfaces as UnsatisfiedLinkError or
				//NoClassDefFoundError, neither of which we want to escape the fallback
				nativeAvailable = false;
				OneLog.log(Level.INFO, "Native HDF5 backend unavailable, falling back to jhdf", t);
			}
		}
		return nativeAvailable;
	}

}
