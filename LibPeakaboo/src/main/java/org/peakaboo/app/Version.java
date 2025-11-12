package org.peakaboo.app;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Properties;
import java.util.logging.Level;

import org.peakaboo.framework.accent.AlphaNumericComparitor;
import org.peakaboo.tier.Tier;

public class Version {
	
	public final static int VERSION_MAJOR = 6;
	public final static int VERSION_MINOR = 1;
	public final static int VERSION_POINT = 0;
	//Program name intended for internal use, filesystem, etc. For user-friendly name, see Tier.appName()
	public final static String PROGRAM_NAME = "Peakaboo";
	
	public final static ReleaseType RELEASE_TYPE = ReleaseType.DEVELOPMENT;
	
	
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
			if (versionInfoFile == null) {
				// We don't log because this is too early in application startup for the logger to be initialized
				System.err.println("WARNING: Version property file not found");
			} else {
				prop.load(versionInfoFile);
				String rawBuildDate = prop.getProperty("builddate");
				// Check if the Maven placeholder hasn't been replaced yet
				if (rawBuildDate != null && !rawBuildDate.contains("${")) {
					buildDate = rawBuildDate;
				} else {
					System.err.println("WARNING: Build date property not populated by Maven build");
				}
			}
		} catch (IOException e) {
			System.err.println("WARNING: Cannot load version property file: " + e.getMessage());
		}

		// For DEV and RC builds, use current date if buildDate is empty
		// For RELEASE builds, leave empty to fail visibly
		if (buildDate.isEmpty() && RELEASE_TYPE != ReleaseType.RELEASE) {
			buildDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		}
	}
	
	
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

	/**
	 * Parses the build date string into a LocalDate object.
	 * The build date format is yyyy-MM-dd as set in the Maven pom.xml.
	 * @return LocalDate representing the build date, or current date if parsing fails
	 */
	public static LocalDate getBuildDate() {
		if (buildDate == null || buildDate.isEmpty()) {
			PeakabooLog.get().log(Level.WARNING, "Build date is empty, using current date");
			return LocalDate.now();
		}

		try {
			return LocalDate.parse(buildDate, DateTimeFormatter.ISO_LOCAL_DATE);
		} catch (DateTimeParseException e) {
			PeakabooLog.get().log(Level.WARNING, "Could not parse build date '" + buildDate + "', using current date", e);
			return LocalDate.now();
		}
	}


}
