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
			
			//filename must not have characters in it which will make windows cry...
			Date ts = new Date();
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			String filename = appDir.getPath() + "/Peakaboo Log for " + formatter.format(ts) + ".log";
			
			
			//Workaround for JDK-8189953
			File file = new File(filename);
			file.createNewFile();
			////////////////////////////
			
			
			FileHandler handler = new FileHandler(filename);
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
