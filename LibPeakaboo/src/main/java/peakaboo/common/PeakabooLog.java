package peakaboo.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.io.output.TeeOutputStream;

public class PeakabooLog {

	private final static String format = "%1$ty-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-7s [%2$s] %5$s %6$s%n";
	private final static Map<String, Logger> loggers = new HashMap<>();
	
	public static void init(File logDir) {
		try {
			removeDefaultHandlers();
			configFileHandler(logDir);
			configStdErr();
		} catch (SecurityException | IOException e) {
			getRoot().log(Level.WARNING, "Cannot create Peakaboo log file", e);
		}
	}

	
	
	private static void configFileHandler(File logDir) throws IOException {
	
		logDir.mkdirs();
		String filename = logDir.getPath() + "/Peakaboo.log";
				
		//Workaround for JDK-8189953
		new File(logDir.getPath() + "/Peakaboo.log").createNewFile();
		////////////////////////////
		
		FileHandler handler = new FileHandler(filename, 16*1024*1024, 1, true);
		handler.setFormatter(new CustomFormatter(format));
		getRoot().addHandler(handler);
	}
	
	
	//All stderr goes to the log and real stderr
	private static void configStdErr() throws FileNotFoundException {
		OutputStream loggingOS = new LoggingOutputStream(getRoot(), Level.SEVERE);
		System.setErr(new PrintStream(new TeeOutputStream(System.err, loggingOS)));
	}
	
	//Logger doesn't print to stderr, stderr prints to the log
	private static void removeDefaultHandlers() {
		Logger rootLogger = getRoot();
		for (Handler handler : rootLogger.getHandlers()) {
			rootLogger.removeHandler(handler);
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
			loggers.put(name, Logger.getLogger(name));
		}
		return loggers.get(name);
	}
	
}


// from http://blog.adeel.io/2017/05/06/redirecting-all-stdout-and-stderr-to-logger-in-java/
class LoggingOutputStream extends OutputStream {
	
	Logger logger;
	Level level;
	StringBuilder stringBuilder;

	public LoggingOutputStream(Logger logger, Level level) {
		this.logger = logger;
		this.level = level;
		this.stringBuilder = new StringBuilder();
	}

	@Override
	public final void write(int i) throws IOException {

		// then out to the logger
		char c = (char) i;
		if (c == '\r' || c == '\n') {
			if (stringBuilder.length() > 0) {
				logger.log(level, stringBuilder.toString());
				stringBuilder = new StringBuilder();
			}
		} else {
			stringBuilder.append(c);
		}
		
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
