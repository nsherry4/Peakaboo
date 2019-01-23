package org.peakaboo.common;



import java.io.File;




public class Env
{

	public enum OS {
		WINDOWS,
		MAC,
		UNIX,
		ANDROID,
		OTHER;
		
		
		public static boolean isWindows()
		{

			String os = System.getProperty("os.name").toLowerCase();
			// windows
			return (os.indexOf("win") >= 0);

		}


		public static boolean isMac()
		{

			String os = System.getProperty("os.name").toLowerCase();
			// Mac
			return (os.indexOf("mac") >= 0);

		}


		public static boolean isUnix()
		{

			String os = System.getProperty("os.name").toLowerCase();
			// linux or unix
			return (!isAndroid()) && (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);

		}
		
		public static boolean isAndroid() {
			return System.getProperty("java.vendor").equals("The Android Project");
		}

		
	}
	

	



	
	public static Env.OS getOS()
	{
		if (OS.isWindows()) return OS.WINDOWS;
		if (OS.isMac()) return OS.MAC;
		if (OS.isUnix()) return OS.UNIX;
		if (OS.isAndroid()) return OS.ANDROID;
		return OS.OTHER;
	}
	
	/**
	 * Returns the maximum size of the heap in megabytes
	 */
	public static long maxHeap()
	{
		return (long)(maxHeapBytes() >> 20);
	}
	
	/**
	 * Returns the maximum size of the heap in bytes
	 */
	public static long maxHeapBytes()
	{
		return Runtime.getRuntime().maxMemory();
	}
	
	
	public static File appDataDirectory(String appname) {
		return appDataDirectory(appname, "");
	}
	
	public static File appDataDirectory(String appname, String subpath)
	{
		switch (getOS())
		{
			case ANDROID: throw new UnsupportedOperationException("Function not supported on Android"); 	
			case WINDOWS: return new File(System.getenv("APPDATA") + "\\" + appname + "\\" + subpath);
			case MAC: return new File(homeDirectory() + "/Library/Application Support/" + appname + "/" + subpath);
					
			case OTHER:
			case UNIX:
			default:
				return new File(homeDirectory() + "/.config/" + appname + "/" + subpath);
		}

	}
	

	public static File homeDirectory() {
		return new File(System.getProperty("user.home"));
	}
	

}
