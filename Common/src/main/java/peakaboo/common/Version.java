package peakaboo.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;

public class Version {

	public static String buildDate = "";
	
	private static final Properties prop;
	static {
		prop = new Properties();
		try {
			InputStream versionInfoFile = Version.class.getResourceAsStream("/peakaboo/common/version.info");
			prop.load(versionInfoFile);
			buildDate = prop.getProperty("builddate");
		} catch (IOException e) {
			PeakabooLog.get().log(Level.WARNING, "Cannot load version property file", e);
		}
	}
	
	
	public final static boolean release = true;
	
	public final static String devReleaseDescription = "Development Version - " + buildDate;
	public final static String releaseDescription = release ? "" : devReleaseDescription;
	
	public final static String titleReleaseDescription = (release ? "" : " [") + releaseDescription + (release ? "" : "]");
	
	
	public final static int versionNoMajor = 5;
	public final static int versionNoMinor = 0;
	public final static int versionNoPoint = 0;
	
	
	public final static String longVersionNo =  
			release ? versionNoMajor + "." + versionNoMinor + "." + versionNoPoint : 
			versionNoMajor + "." + versionNoMinor + "." + versionNoPoint + "dev";
	public final static String logo = (release) ? "icon" : "devicon";
	public final static String icon = (release) ? "icon" : "devicon";
	public final static String splash = (release) ? "splash" : "devsplash";
	public final static String program_name = "Peakaboo";
	public final static String title = program_name + " " + (release ? versionNoMajor : longVersionNo) + titleReleaseDescription;
	

	
	
	
}
