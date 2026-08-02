package org.peakaboo.framework.accent;



import org.peakaboo.framework.accent.log.OneLog;

import java.io.File;
import java.util.logging.Level;


/**
 * Platform specific code, limited to headless information
 */
public class Platform
{

	public enum OS {
		WINDOWS,
		MAC,
		UNIX,
		ANDROID,
		OTHER;
		
		
		public static boolean isWindows() {
			String os = System.getProperty("os.name").toLowerCase();
			return (os.indexOf("win") >= 0);
		}


		public static boolean isMac() {
			String os = System.getProperty("os.name").toLowerCase();
			return (os.indexOf("mac") >= 0);
		}


		public static boolean isUnix() {
			String os = System.getProperty("os.name").toLowerCase();
			return (!isAndroid()) && (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
		}
		
		public static boolean isAndroid() {
			return System.getProperty("java.vendor").equals("The Android Project");
		}

		
	}
	

	



	
	public static Platform.OS getOS() {
		if (OS.isWindows()) return OS.WINDOWS;
		if (OS.isMac()) return OS.MAC;
		if (OS.isUnix()) return OS.UNIX;
		if (OS.isAndroid()) return OS.ANDROID;
		return OS.OTHER;
	}
	
	/**
	 * Returns the maximum size of the heap in megabytes
	 */
	public static long maxHeap() {
		return (long)(maxHeapBytes() >> 20);
	}
	
	/**
	 * Returns the maximum size of the heap in bytes
	 */
	public static long maxHeapBytes() {
		return Runtime.getRuntime().maxMemory();
	}
	
	/**
	 * Convenience method for {@link Runtime#availableProcessors()}
	 * @return
	 */
	public static int cores() {
		return Runtime.getRuntime().availableProcessors();
	}
	
	
	public static File appDir(String appname) {
		return appDirEntry(appname, "");
	}
	
	public static File appDirEntry(String appname, String subpath) {
		appname = appname.toLowerCase();
		switch (getOS()) {
			case ANDROID: throw new UnsupportedOperationException("Function not supported on Android"); 	
			case WINDOWS: return new File(System.getenv("APPDATA") + "\\" + appname + "\\" + subpath);
			case MAC: return new File(homeDirectory() + "/Library/Application Support/" + appname + "/" + subpath);
					
			case OTHER:
			case UNIX:
			default:
				return new File(homeDirectory() + "/.local/" + appname + "/" + subpath);
		}

	}
	
	/** What is the jpackage'd install directory? */
	public static File installDir() {
		String apppath = System.getProperty("jpackage.app-path");
		if (apppath == null) return null;
		var pathfile = new File(apppath);
		if (getOS() == OS.MAC) {
			return new File(new File(pathfile.getParent()).getParent());
		} else {
			return new File(pathfile.getParent());
		}
	}
	
	/** What is the name of the jpackage'd app? */
	public static String launcherName() {
		String apppath = System.getProperty("jpackage.app-path");
		if (apppath == null) return null;
		String name = new File(apppath).getName();
		if (name.toLowerCase().endsWith(".exe")) {
			name = name.substring(0, name.length() - 4);
		}
		return name;
	}

	/** What is the package name that jpackage generates and installs to? */
	public static String packageName() {
		String name = launcherName();
		if (name == null) return null;
		return switch(getOS()) {
			// Match the transform on the name done by the package manager
			case UNIX, OTHER -> name.toLowerCase().replaceAll("[ _]", "-");
			// No transformation on the name expected
			case WINDOWS, MAC -> name;
			case ANDROID -> throw new UnsupportedOperationException("Unimplemented OS: " + getOS());
		};
	}

	public static File systemCFGFile() {
		File install = installDir();
		String launcher = launcherName();
		if (install == null || launcher == null) {
			throw new RuntimeException("Not running from a jpackage install, cannot locate the system CFG file");
		}
		OneLog.log(Level.INFO, "Detected install directory as " + install.getPath());
		File cfg = switch(getOS()) {
			case ANDROID -> throw new UnsupportedOperationException("Unimplemented OS: " + getOS());
			case WINDOWS -> new File(install.getPath() + "/app/" + launcher + ".cfg");
			case MAC -> new File(install.getPath() + "/app/" + launcher + ".cfg");
			case UNIX -> new File(install.getPath() + "/../lib/app/" + launcher + ".cfg");
			case OTHER -> throw new UnsupportedOperationException("Unimplemented OS: " + getOS());
		};
		if (!cfg.exists()) {
			throw new RuntimeException("System CFG file not found at expected location: " + cfg.getPath());
		}
		return cfg;
	}

	/**
	 * Location of the jpackage user override config file. Based on jpackage app name,
	 * rather than an applications self-given name.
	 */
	public static File userCFGFile() {
		String launcher = launcherName();
		if (launcher == null) {
			throw new RuntimeException("Cannot locate the user CFG file");
		}
		return appDirEntry(packageName(), launcher + ".cfg");
	}

	public static File homeDirectory() {
		return new File(System.getProperty("user.home"));
	}
	

}
