package org.peakaboo.app;



import java.io.File;




public class Env
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
	

	



	
	public static Env.OS getOS() {
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
	
	
	public static File systemCFGFile(String appname) {
		return switch(getOS()) {
			case ANDROID -> throw new UnsupportedOperationException("Unimplemented case: " + getOS());
			case WINDOWS -> throw new UnsupportedOperationException("Unimplemented case: " + getOS());
			case MAC -> throw new UnsupportedOperationException("Unimplemented case: " + getOS());
			case OTHER -> throw new UnsupportedOperationException("Unimplemented case: " + getOS());
			case UNIX -> new File("/opt/" + appname.toLowerCase() + "/lib/app" + appname + ".cfg");
			default -> throw new IllegalArgumentException("Unexpected value: " + getOS());
		};
	}

	public static File homeDirectory() {
		return new File(System.getProperty("user.home"));
	}
	

}
