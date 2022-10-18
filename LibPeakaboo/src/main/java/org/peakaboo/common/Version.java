package org.peakaboo.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.logging.Level;

import org.peakaboo.framework.bolt.plugin.core.AlphaNumericComparitor;
import org.peakaboo.tier.Tier;

public class Version {

	public enum ReleaseType {
		DEVELOPMENT,
		CANDIDATE,
		RELEASE
	}
	
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
	
	
	public final static int versionNoMajor = 5;
	public final static int versionNoMinor = 5;
	public final static int versionNoPoint = 1;
	//Program name intended for internal use, filesystem, etc. For user-friendly name, see Tier.appName()
	public final static String program_name = "Peakaboo";
	
	public final static ReleaseType releaseType = ReleaseType.DEVELOPMENT;
	


	
	public final static String releaseDescription;
	public final static String titleReleaseDescription;
	public final static String longVersionNo;
	public final static String logo, splash;
	public final static String title;
	
	
	static {
		switch (releaseType) {
		
		default:
		case DEVELOPMENT:
			releaseDescription = Tier.provider().tierName() + " Development Version - " + buildDate;
			titleReleaseDescription = "[" + releaseDescription + "]";
			longVersionNo = versionNoMajor + "." + versionNoMinor + "." + versionNoPoint + "dev";
			logo = "devicon";
			splash = "devsplash";
			title = Tier.provider().appName() + longVersionNo + titleReleaseDescription;
			break;
			
		case CANDIDATE:
			releaseDescription = Tier.provider().tierName() + " Release Candidate";
			titleReleaseDescription = "[" + releaseDescription + "]";
			longVersionNo = versionNoMajor + "." + versionNoMinor + "." + versionNoPoint + "rc";
			logo = "rcicon";
			splash = "rcsplash";
			title = Tier.provider().appName() + versionNoMajor + "." + versionNoMinor + titleReleaseDescription;
			break;

		case RELEASE:
			releaseDescription = Tier.provider().tierName();
			titleReleaseDescription = "";
			longVersionNo = versionNoMajor + "." + versionNoMinor + "." + versionNoPoint;
			title = Tier.provider().appName() + versionNoMajor + titleReleaseDescription;
			logo = "icon";
			splash = "splash";
			break;
			
		}
	};

	




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
