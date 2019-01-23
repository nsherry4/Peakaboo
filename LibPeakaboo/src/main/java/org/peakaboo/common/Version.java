package org.peakaboo.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.logging.Level;

import net.sciencestudio.bolt.plugin.core.AlphaNumericComparitor;

public class Version {

	public static String buildDate = "";
	
	private static final Properties prop;
	static {
		prop = new Properties();
		try {
			InputStream versionInfoFile = Version.class.getResourceAsStream("/org/peakaboo/common/version.info");
			prop.load(versionInfoFile);
			buildDate = prop.getProperty("builddate");
		} catch (IOException e) {
			PeakabooLog.get().log(Level.WARNING, "Cannot load version property file", e);
		}
	}
	
	
	public final static boolean release = false;
	
	public final static String devReleaseDescription = "Development Version - " + buildDate;
	public final static String releaseDescription = release ? "" : devReleaseDescription;
	
	public final static String titleReleaseDescription = (release ? "" : " [") + releaseDescription + (release ? "" : "]");
	
	
	public final static int versionNoMajor = 5;
	public final static int versionNoMinor = 2;
	public final static int versionNoPoint = 1;
	
	
	public final static String longVersionNo =  
			release ? versionNoMajor + "." + versionNoMinor + "." + versionNoPoint : 
			versionNoMajor + "." + versionNoMinor + "." + versionNoPoint + "dev";
	public final static String logo = (release) ? "icon" : "devicon";
	public final static String icon = (release) ? "icon" : "devicon";
	public final static String splash = (release) ? "splash" : "devsplash";
	public final static String program_name = "Peakaboo";
	public final static String title = program_name + " " + (release ? versionNoMajor : longVersionNo) + titleReleaseDescription;
	

	public static boolean hasNewVersion() {
		
		String thisVersion = longVersionNo;
		String otherVersion = "";
		
		try {
			URL website = new URL("https://raw.githubusercontent.com/nsherry4/Peakaboo/master/version");
			URLConnection conn = website.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
	        while ((inputLine = in.readLine()) != null) {
	        	otherVersion += inputLine + "\n";
	        }
	        otherVersion = otherVersion.trim();
	        in.close();
	        
			AlphaNumericComparitor cmp = new AlphaNumericComparitor(false);
			return cmp.compare(thisVersion, otherVersion) < 0;
		} catch (IOException e) {
			PeakabooLog.get().log(Level.WARNING, "Could not check for new version", e);
			return false;
		}
	}
	
	
	
}
