package peakaboo.common;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class PeakabooLog {

	public static void init() {
		try {
			File appDir = Configuration.appDir();
			appDir.mkdirs();
			FileHandler handler = new FileHandler(appDir.getPath() + "/Peakaboo.log");
			handler.setFormatter(new SimpleFormatter());
			Logger.getLogger("").addHandler(handler);
		} catch (SecurityException | IOException e) {
			Logger.getLogger("").log(Level.WARNING, "Cannot create Peakaboo log file", e);
		}
	}
	
	public static Logger get() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		Logger logger = Logger.getLogger( stElements[0].getClassName() );
		return logger;
	}
	
}
