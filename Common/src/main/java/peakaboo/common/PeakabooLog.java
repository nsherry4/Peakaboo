package peakaboo.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.output.TeeOutputStream;

public class PeakabooLog {

	private final static String format = "%1$ty-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-7s [%2$s] %5$s %6$s%n";
	
	public static void init() {
		try {
			removeDefaultHandlers();
			configFileHandler();
			configStdErr();
		} catch (SecurityException | IOException e) {
			Logger.getLogger("").log(Level.WARNING, "Cannot create Peakaboo log file", e);
		}
	}

	
	
	private static void configFileHandler() throws IOException {
	
		File appDir = Configuration.appDir("Logging");
		appDir.mkdirs();
		String filename = appDir.getPath() + "/Peakaboo.log";
				
		//Workaround for JDK-8189953
		new File(appDir.getPath() + "/Peakaboo.log").createNewFile();
		////////////////////////////
		
		FileHandler handler = new FileHandler(filename, 128*1024*1024, 1, true);
		handler.setFormatter(new CustomFormatter(format));
		Logger.getLogger("").addHandler(handler);
	}
	
	
	//All stderr goes to the log and real stderr
	private static void configStdErr() throws FileNotFoundException {
		OutputStream loggingOS = new LoggingOutputStream(Logger.getLogger(""), Level.SEVERE);
		System.setErr(new PrintStream(new TeeOutputStream(System.err, loggingOS)));
	}
	
	//Logger doesn't print to stderr, stderr prints to the log
	private static void removeDefaultHandlers() {
		Logger rootLogger = Logger.getLogger("");
		for (Handler handler : rootLogger.getHandlers()) {
			rootLogger.removeHandler(handler);
		}
	}
	
	
	public static Logger get() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		Logger logger = Logger.getLogger( stElements[0].getClassName() );
		return logger;
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
		return String.format(
			format, 
			new java.util.Date(record.getMillis()), 
			className,
			record.getLoggerName(), 
			record.getLevel().getLocalizedName(), 
			record.getMessage(),
			String.valueOf(record.getThrown())
		);
	}

}
