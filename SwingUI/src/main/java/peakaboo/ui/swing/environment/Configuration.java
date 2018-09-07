package peakaboo.ui.swing.environment;

import java.io.File;

import peakaboo.common.Env;
import peakaboo.common.Version;

public class Configuration {

	public static File appDir() {
		return Env.appDataDirectory(Version.program_name + Version.versionNoMajor);
	}
	public static File appDir(String subdir) {
		return Env.appDataDirectory(Version.program_name + Version.versionNoMajor, subdir);
	}
}
