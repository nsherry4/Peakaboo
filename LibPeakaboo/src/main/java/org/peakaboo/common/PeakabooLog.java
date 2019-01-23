package peakaboo.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class PeakabooLog {

	private final static String format = "%1$ty-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-7s [%2$s] %5$s %6$s%n";
	private final static Map<String, Logger> loggers = new HashMap<>();
	private static boolean initted = false;
	private static String logfilename;
	private static Level logLevel;
	
	public synchronized static void init(File logDir) {
		if (initted == true) {
			return;
		}
		initted = true;
		
		String logLevelString = "INFO";
		logLevelString = System.getProperty("java.util.logging.loglevel", logLevelString);
		logLevel = Level.parse(logLevelString);
		
		Properties props = new Properties();
		props.setProperty("java.util.logging.loglevel", logLevelString);
		props.setProperty("java.util.logging.ConsoleHandler.level", logLevelString);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(512);
		try {
			props.store(bos, "No Comment");
			bos.flush();
			LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(bos.toByteArray()));
		} catch (IOException e1) {
			get().log(Level.SEVERE, "Failed to configure logging", e1);
		}
		
		
		//Set default behaviour for uncaught errors to here
		Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
			get().log(Level.SEVERE, "Uncaught Exception", e);
		});
		
		//remove default handlers
		for (Handler handler : getRoot().getHandlers()) {
			getRoot().removeHandler(handler);
		}
		
		//add console handler
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(logLevel);
		getRoot().addHandler(consoleHandler);
		
		//Set up log file handler
		configFileHandler(logDir);


		
	}



	private static void configFileHandler(File logDir) {
	
		try {
			logDir.mkdirs();
			logfilename = logDir.getPath() + "/Peakaboo.log";
					
			//Workaround for JDK-8189953
			new File(logDir.getPath() + "/Peakaboo.log").createNewFile();
			////////////////////////////
			
			FileHandler handler = new FileHandler(logfilename, 16*1024*1024, 1, true);
			handler.setFormatter(new CustomFormatter(format));
			handler.setLevel(logLevel);
			getRoot().addHandler(handler);
		} catch (SecurityException | IOException e) {
			getRoot().log(Level.WARNING, "Cannot create Peakaboo log file", e);
		}
	}
		
	
	public static Logger getRoot() {
		return get("");
	}
	
	public static Logger get() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		String name = stElements[2].getClassName();
		return get(name);
	}
	
	private static Logger get(String name) {
		if (!loggers.containsKey(name)) {
			Logger logger = Logger.getLogger(name);
			logger.setLevel(logLevel);
			loggers.put(name, logger);
		}
		return loggers.get(name);
	}



	public static String getLogFilename() {
		return logfilename;
	}
	
	
	
}


//Cannot get SimpleFormatter to honour the SimpleFormatter.format property. 
//It ignores both LogManager and System property values.
class CustomFormatter extends Formatter {

	private String format;

	public CustomFormatter(String format) {
		this.format = format;
	}

	@Override
	public String format(LogRecord record) {
		String className = "";
		if (record.getSourceClassName().contains(".")) {
			String[] clsParts = record.getSourceClassName().split("\\.");
			className = clsParts[clsParts.length-1] + ":" + record.getSourceMethodName();
		} else {
			className = record.getSourceClassName() + ":" + record.getSourceMethodName();
		}
		
		String thrown = "";
		if (record.getThrown() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			record.getThrown().printStackTrace(pw);
			pw.flush();
			thrown = sw.toString();
		}
		
		
		return String.format(
			format, 
			new java.util.Date(record.getMillis()), 
			className,
			record.getLoggerName(), 
			record.getLevel().getLocalizedName(), 
			record.getMessage(),
			thrown
		);
	}

}
