package org.peakaboo.framework.accent.log;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * In-memory log handler that maintains a circular buffer of recent log entries.
 * <p>
 * This handler is useful for displaying recent logs in crash reports or diagnostic
 * dialogs without requiring file system access. The buffer maintains up to 100
 * recent log entries, automatically discarding the oldest entries when the limit
 * is reached.
 * <p>
 * This handler is thread-safe and can be accessed concurrently.
 */
public class OneBufferHandler extends Handler {

	/** The formatter used to convert log records to strings */
	private Formatter formatter;

	/** Circular buffer storing the most recent formatted log entries */
	private final String[] entries;

	/** Current write position in the circular buffer */
	private int writePos = 0;

	/** Number of entries written (used to determine if buffer has wrapped) */
	private int entryCount = 0;

	/** Capacity of the circular buffer */
	private static final int CAPACITY = 100;

	/**
	 * Creates a new buffer handler that stores up to 100 recent log entries.
	 *
	 * @param formatter the formatter to use for log record formatting
	 */
	public OneBufferHandler(Formatter formatter) {
		this.formatter = formatter;
		this.entries = new String[CAPACITY];
	}

	/**
	 * Sets the formatter for this handler.
	 * This method allows the formatter to be updated after construction.
	 *
	 * @param formatter the new formatter to use
	 */
	@Override
	public synchronized void setFormatter(Formatter formatter) {
		this.formatter = formatter;
		super.setFormatter(formatter);
	}

	/**
	 * No-op implementation required by {@link Handler} contract.
	 */
	@Override
	public void close() throws SecurityException {
	}

	/**
	 * No-op implementation required by {@link Handler} contract.
	 * The buffer doesn't require flushing as entries are stored in memory.
	 */
	@Override
	public void flush() {
	}

	/**
	 * Publishes a log record by formatting it and adding to the circular buffer.
	 * Older entries are automatically discarded when the buffer capacity is exceeded.
	 *
	 * @param logEntry the log record to publish
	 */
	@Override
	public synchronized void publish(LogRecord logEntry) {
		if (!isLoggable(logEntry)) {
			return;
		}

		String entryText = formatter.format(logEntry);
		entries[writePos] = entryText;
		writePos = (writePos + 1) % CAPACITY;
		entryCount++;
	}

	/**
	 * Retrieves all recent log entries as a single formatted string.
	 * Entries are returned in chronological order (oldest first).
	 *
	 * @return concatenated string of recent log entries
	 */
	public synchronized String getRecentLogs() {
		StringBuilder builder = new StringBuilder();

		// Determine how many entries we have
		int count = Math.min(entryCount, CAPACITY);

		// If buffer has wrapped, start from oldest entry
		int startPos = entryCount > CAPACITY ? writePos : 0;

		// Read entries in chronological order
		for (int i = 0; i < count; i++) {
			int pos = (startPos + i) % CAPACITY;
			if (entries[pos] != null) {
				builder.append(entries[pos]);
			}
		}

		return builder.toString();
	}
}