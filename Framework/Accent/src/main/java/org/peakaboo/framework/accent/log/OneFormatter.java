package org.peakaboo.framework.accent.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Custom log formatter that provides consistent log entry formatting across all handlers.
 * <p>
 * This formatter is necessary because {@link java.util.logging.SimpleFormatter} does not
 * reliably honour the {@code SimpleFormatter.format} property from either {@link java.util.logging.LogManager}
 * configuration or system properties.
 * <p>
 * The formatter extracts the simple class name from fully-qualified class names and combines
 * it with the method name for more readable log output. When exceptions are logged, their
 * full stack traces are appended to the log entry.
 */
public class OneFormatter extends Formatter {

	/** The format string to use for formatting log records */
	private String format;

	/**
	 * Creates a new formatter with the specified format string.
	 *
	 * @param format the format string compatible with {@link java.util.Formatter}
	 */
	public OneFormatter(String format) {
		this.format = format;
	}

	/**
	 * Formats a log record into a human-readable string.
	 *
	 * @param entry the log record to format
	 * @return formatted log entry string
	 */
	@Override
	public String format(LogRecord entry) {
		// Extract simple class name from fully-qualified name
		String className;
		if (entry.getSourceClassName().contains(".")) {
			String[] clsParts = entry.getSourceClassName().split("\\.");
			className = clsParts[clsParts.length-1] + ":" + entry.getSourceMethodName();
		} else {
			className = entry.getSourceClassName() + ":" + entry.getSourceMethodName();
		}

		// Format exception stack trace if present
		String thrown = "";
		if (entry.getThrown() != null) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			entry.getThrown().printStackTrace(pw);
			pw.flush();
			thrown = sw.toString();
		}

		return String.format(
			format,
			new java.util.Date(entry.getMillis()),
			className,
			entry.getLoggerName(),
			entry.getLevel().getLocalizedName(),
			entry.getMessage(),
			thrown
		);
	}
}