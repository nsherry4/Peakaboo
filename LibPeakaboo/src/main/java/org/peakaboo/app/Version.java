package org.peakaboo.app;

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
	
	
	public final static int VERSION_MAJOR = 6;
	public final static int VERSION_MINOR = 1;
	public final static int VERSION_POINT = 0;
	//Program name intended for internal use, filesystem, etc. For user-friendly name, see Tier.appName()
	public final static String PROGRAM_NAME = "Peakaboo";
	
	public final static ReleaseType RELEASE_TYPE = ReleaseType.RELEASE;
	


	
	public final static String RELEASE_DESCRIPTION;
	public final static String RELEASE_TITLE;
	public final static String LONG_VERSION;
	public final static String LOGO, SPLASH;
	public final static String APP_TITLE;
	
	
	static {
		switch (RELEASE_TYPE) {
		
		default:
		case DEVELOPMENT:
			RELEASE_DESCRIPTION = Tier.provider().tierName() + " Development Version - " + buildDate;
			RELEASE_TITLE = "[" + RELEASE_DESCRIPTION + "]";
			LONG_VERSION = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_POINT + "dev";
			LOGO = "devicon";
			SPLASH = "devsplash";
			APP_TITLE = Tier.provider().appName() + LONG_VERSION + RELEASE_TITLE;
			break;
			
		case CANDIDATE:
			RELEASE_DESCRIPTION = Tier.provider().tierName() + " Release Candidate";
			RELEASE_TITLE = "[" + RELEASE_DESCRIPTION + "]";
			LONG_VERSION = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_POINT + "rc";
			LOGO = "rcicon";
			SPLASH = "rcsplash";
			APP_TITLE = Tier.provider().appName() + VERSION_MAJOR + "." + VERSION_MINOR + RELEASE_TITLE;
			break;

		case RELEASE:
			RELEASE_DESCRIPTION = Tier.provider().tierName();
			RELEASE_TITLE = "";
			LONG_VERSION = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_POINT;
			APP_TITLE = Tier.provider().appName() + VERSION_MAJOR + RELEASE_TITLE;
			LOGO = "icon";
			SPLASH = "splash";
			break;
			
		}
	};

	




	public static boolean hasNewVersion() {
		String[] addresses = new String[] {
				"https://peakaboo.org/appdata/org.peakaboo/version",
				"https://raw.githubusercontent.com/nsherry4/Peakaboo/master/version"
		};
		
		for (String address : addresses) {
			try {
				return hasNewVersion(new URL(address));
			} catch (IOException e) {
				//Ignore a single failure
			}
		}
		
		PeakabooLog.get().log(Level.WARNING, "Could not check for new version from any known url");
		return false;
		
	}
	
	private static boolean hasNewVersion(URL url) throws IOException {
		
		String thisVersion = LONG_VERSION;
		String otherVersion = "";
		

		URLConnection conn = url.openConnection();
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

	}
	
	
	
}
