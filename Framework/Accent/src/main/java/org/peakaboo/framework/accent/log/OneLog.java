package org.peakaboo.framework.accent.log;

import org.peakaboo.framework.accent.Platform;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Centralized logging facade providing a unified interface for Java Util Logging.
 * <p>
 * Features:
 * <ul>
 *   <li>Zero-configuration initialization - works immediately on first use</li>
 *   <li>Automatic caller class detection via stack trace inspection</li>
 *   <li>Optional file-based logging with rotation</li>
 *   <li>Console output (automatically disabled on Android)</li>
 *   <li>In-memory circular buffer for recent log retrieval</li>
 *   <li>Dynamic verbosity control</li>
 *   <li>Global uncaught exception handling</li>
 * </ul>
 *
 * <p>
 * <b>Basic usage:</b>
 * <pre>
 * Logger log = OneLog.get();
 * log.info("Application started");
 * </pre>
 *
 * <p>
 * <b>Desktop application with file logging:</b>
 * <pre>
 * OneLog.setAppInfo("MyApp", "1.0.0");
 * OneLog.addFileHandler(new File("/var/log/myapp"));
 * Logger log = OneLog.get();
 * </pre>
 */
public class OneLog {

	/**
	 * Log entry format string for {@link java.util.Formatter}.
	 * Format parameters:
	 * %1 = timestamp (Date)
	 * %2 = class:method name
	 * %3 = logger name
	 * %4 = log level
	 * %5 = message
	 * %6 = stack trace (if exception present)
	 */
	private static final String LOG_FORMAT_TEMPLATE = "[%s] %%1$ty-%%1$tm-%%1$td %%1$tH:%%1$tM:%%1$tS %%4$-7s [%%2$s] %%5$s %%6$s%%n";

	/**
	 * Cache of created loggers keyed by class name.
	 * <p>
	 * This cache exists to support dynamic verbosity changes via {@link #setVerbose(boolean)}.
	 * When verbosity is changed, all cached loggers must be updated with the new level.
	 * Without this cache, loggers stored in static fields (the standard pattern) would
	 * retain their old log levels and miss the verbosity change.
	 */
	private static final Map<String, Logger> LOGGER_CACHE = new HashMap<>();

	/** Initialization guard to ensure lazy initialization only runs once */
	private static boolean initialized = false;

	/** Current verbosity setting - true enables FINE level, false uses INFO level */
	private static boolean verbose = false;

	/** Application name for log formatting, defaults to empty string */
	private static String appName = "";

	/** Application version for log formatting, defaults to empty string */
	private static String appVersion = "";

	/** Path to the log file, set when file handler is added */
	private static String logFilename;

	/** In-memory circular buffer handler for recent log retrieval */
	private static OneBufferHandler bufferHandler;

	/** File-based log handler with rotation support */
	private static FileHandler fileHandler;

	/** Console output handler */
	private static ConsoleHandler consoleHandler;

	/** Private constructor prevents instantiation of this utility class */
	private OneLog() {
	}

	/**
	 * Returns a logger instance for the calling class. The class name is determined
	 * automatically via stack trace inspection.
	 * <p>
	 * This method performs lazy initialization on first call, setting up console,
	 * buffer, and exception handlers with default configuration.
	 * <p>
	 * The returned logger's level is set according to the current verbosity setting.
	 * When {@link #setVerbose(boolean)} is called, all cached loggers are updated.
	 *
	 * @return logger instance named after the calling class
	 */
	public static synchronized Logger get() {
		if (!initialized) {
			initialize();
		}
		return get(getCallerFromStackTrace().getClassName());
	}

	/**
	 * Returns the root logger (unnamed logger).
	 *
	 * @return the root logger instance
	 */
	public static Logger getRoot() {
		return get("");
	}

	
	
	/**
	 * Sets application name and version for log formatting.
	 * <p>
	 * If not called, logs will use empty strings for app name and version.
	 * This method can be called before or after initialization.
	 *
	 * @param name the application name
	 * @param version the application version
	 */
	public static synchronized void setAppInfo(String name, String version) {
		OneLog.appName = name;
		OneLog.appVersion = version;

		// If already initialized, update the formatter
		if (initialized) {
			String format = buildLogFormat();
			OneFormatter formatter = new OneFormatter(format);

			if (bufferHandler != null) {
				bufferHandler.setFormatter(formatter);
			}
			if (fileHandler != null) {
				fileHandler.setFormatter(formatter);
			}
		}
	}

	/**
	 * Adds a file-based log handler with rotation support.
	 * Creates a single rotating log file with a maximum size of 50 MB.
	 * <p>
	 * This method is typically called only on desktop platforms. Android applications
	 * should use logcat instead.
	 *
	 * @param logDir the directory where the log file will be created; will be created if it doesn't exist
	 */
	public static synchronized void addFileHandler(File logDir, String logFilename) {
		if (!initialized) {
			initialize();
		}

		try {
			logDir.mkdirs();
			logFilename = logDir.getPath() + "/" + logFilename;

			// Workaround for JDK-8189953: FileHandler fails on first run if file doesn't exist
			// See: https://bugs.openjdk.org/browse/JDK-8189953
			new File(logFilename).createNewFile();

			// 50 MB limit, 1 file, append mode
			fileHandler = new FileHandler(logFilename, 50*1024*1024, 1, true);
			fileHandler.setFormatter(new OneFormatter(buildLogFormat()));
			fileHandler.setLevel(getLevel());
			getRoot().addHandler(fileHandler);
		} catch (SecurityException | IOException e) {
			getRoot().log(Level.WARNING, "Cannot create log file", e);
		}
	}

	/**
	 * Changes the verbosity level for all loggers.
	 * <p>
	 * When verbose is true, loggers use FINE level (detailed logging).
	 * When verbose is false, loggers use INFO level (normal logging).
	 * <p>
	 * This method updates all cached loggers and handlers to ensure the new
	 * level takes effect immediately, even for loggers stored in static fields.
	 *
	 * @param verbose true for FINE level, false for INFO level
	 */
	public static synchronized void setVerbose(boolean verbose) {
		OneLog.verbose = verbose;
		Level level = getLevel();

		// Update root logger
		getRoot().setLevel(level);

		// Update all cached loggers
		for (Logger logger : LOGGER_CACHE.values()) {
			logger.setLevel(level);
		}

		// Update handlers
		if (consoleHandler != null) {
			consoleHandler.setLevel(level);
		}
		if (bufferHandler != null) {
			bufferHandler.setLevel(level);
		}
		if (fileHandler != null) {
			fileHandler.setLevel(level);
		}
	}

	/**
	 * Retrieves recent log entries from the in-memory circular buffer.
	 *
	 * @return formatted string containing the most recent log entries (up to 100)
	 */
	public static String getRecentLogs() {
		if (bufferHandler == null) {
			return "";
		}
		return bufferHandler.getRecentLogs();
	}

	/**
	 * Returns the absolute path to the log file, if file logging has been enabled.
	 *
	 * @return log file path, or null if file logging has not been configured
	 */
	public static String getLogFilename() {
		return logFilename;
	}

	/**
	 * Convenience method to log a message at INFO level.
	 * Automatically determines the calling class and method via stack trace inspection.
	 *
	 * @param message the message to log
	 */
	public static void log(String message) {
		StackTraceElement caller = getCallerFromStackTrace();
		get(caller.getClassName()).logp(Level.INFO, caller.getClassName(), caller.getMethodName(), message);
	}

	/**
	 * Convenience method to log a message at a specific level.
	 * Automatically determines the calling class and method via stack trace inspection.
	 *
	 * @param level the log level
	 * @param message the message to log
	 */
	public static void log(Level level, String message) {
		StackTraceElement caller = getCallerFromStackTrace();
		get(caller.getClassName()).logp(level, caller.getClassName(), caller.getMethodName(), message);
	}

	/**
	 * Convenience method to log a message with an exception at a specific level.
	 * Automatically determines the calling class and method via stack trace inspection.
	 *
	 * @param level the log level
	 * @param message the message to log
	 * @param thrown the exception to log
	 */
	public static void log(Level level, String message, Throwable thrown) {
		StackTraceElement caller = getCallerFromStackTrace();
		get(caller.getClassName()).logp(level, caller.getClassName(), caller.getMethodName(), message, thrown);
	}

	/**
	 * Platform detection method for Android compatibility.
	 * @return true if running on Android, false otherwise
	 */
	protected static boolean isAndroid() {
		return Platform.getOS().equals(Platform.OS.ANDROID);
	}

	/**
	 * Performs lazy initialization of the logging system.
	 * Sets up console (if not Android), buffer, and uncaught exception handlers.
	 */
	private static void initialize() {
		initialized = true;

		// Configure LogManager properties programmatically (properties file approach doesn't work reliably)
		Properties props = new Properties();
		props.setProperty("java.util.logging.loglevel", getLevel().getName());
		props.setProperty("java.util.logging.ConsoleHandler.level", getLevel().getName());

		ByteArrayOutputStream bos = new ByteArrayOutputStream(512);
		try {
			props.store(bos, "No Comment");
			bos.flush();
			LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(bos.toByteArray()));
		} catch (IOException e) {
			// Can't log this since we're initializing the logger
			e.printStackTrace();
		}

		// Capture all uncaught exceptions in any thread
		Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) ->
			get().log(Level.SEVERE, "Uncaught Exception", e)
		);

		// Remove default JUL handlers to avoid duplicate logging
		for (Handler handler : getRoot().getHandlers()) {
			getRoot().removeHandler(handler);
		}

		// Add console output handler (skip on Android)
		if (!isAndroid()) {
			consoleHandler = new ConsoleHandler();
			consoleHandler.setLevel(getLevel());
			getRoot().addHandler(consoleHandler);
		}

		// Add in-memory circular buffer for recent log retrieval
		bufferHandler = new OneBufferHandler(new OneFormatter(buildLogFormat()));
		bufferHandler.setLevel(getLevel());
		getRoot().addHandler(bufferHandler);
	}

	/**
	 * Returns a cached logger instance for the given name, creating and caching it if necessary.
	 *
	 * @param name the logger name (typically a fully-qualified class name or empty string for root)
	 * @return logger instance for the given name
	 */
	private static Logger get(String name) {
		if (!LOGGER_CACHE.containsKey(name)) {
			Logger logger = Logger.getLogger(name);
			logger.setLevel(getLevel());
			LOGGER_CACHE.put(name, logger);
		}
		return LOGGER_CACHE.get(name);
	}

	/**
	 * Determines the current log level based on verbosity setting.
	 *
	 * @return {@link Level#FINE} if verbose logging is enabled, otherwise {@link Level#INFO}
	 */
	private static Level getLevel() {
		return verbose ? Level.FINE : Level.INFO;
	}

	/**
	 * Gets the calling stack trace element (caller of the method that calls this).
	 * Assumes one level of indirection.
	 *
	 * @return the stack trace element of the caller
	 */
	private static StackTraceElement getCallerFromStackTrace() {
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		return stElements[3];
	}
	
	/**
	 * Builds the log format string with current app name and version.
	 *
	 * @return formatted log format string
	 */
	private static String buildLogFormat() {
		String appInfo = appName.isEmpty() && appVersion.isEmpty()
			? ""
			: appName + " " + appVersion;
		return String.format(LOG_FORMAT_TEMPLATE, appInfo);
	}
}