package peakaboo.common;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class PeakabooLog {

	public static void init() {
		try {
			File appDir = Configuration.appDir("Logging");
			appDir.mkdirs();

			String filename = appDir.getPath() + "/Peakaboo.%g.log";
			
			
			//Workaround for JDK-8189953
			new File(appDir.getPath() + "/Peakaboo.0.log").createNewFile();
			new File(appDir.getPath() + "/Peakaboo.1.log").createNewFile();
			////////////////////////////
			
			
			FileHandler handler = new FileHandler(filename, 128*1024*1024, 2, true);
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
