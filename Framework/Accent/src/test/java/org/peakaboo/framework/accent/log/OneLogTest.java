package org.peakaboo.framework.accent.log;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

/**
 * Basic tests for OneLog functionality.
 */
public class OneLogTest {

	@Test
	public void testBasicLogging() {
		Logger log = OneLog.get();
		assertNotNull(log, "Logger should not be null");

		log.info("Test info message");
		log.warning("Test warning message");

		String recentLogs = OneLog.getRecentLogs();
		assertNotNull(recentLogs, "Recent logs should not be null");
		assertTrue(recentLogs.contains("Test info message"), "Recent logs should contain info message");
		assertTrue(recentLogs.contains("Test warning message"), "Recent logs should contain warning message");
	}

	@Test
	public void testVerbosityChange() {
		Logger log = OneLog.get();

		// Default should be INFO level
		assertFalse(log.isLoggable(Level.FINE), "FINE should not be loggable by default");

		// Enable verbose
		OneLog.setVerbose(true);
		assertTrue(log.isLoggable(Level.FINE), "FINE should be loggable when verbose is true");

		// Disable verbose
		OneLog.setVerbose(false);
		assertFalse(log.isLoggable(Level.FINE), "FINE should not be loggable when verbose is false");
	}

	@Test
	public void testAppInfo() {
		OneLog.setAppInfo("TestApp", "1.0.0");

		Logger log = OneLog.get();
		log.info("Test with app info");

		String recentLogs = OneLog.getRecentLogs();
		assertTrue(recentLogs.contains("TestApp 1.0.0"), "Recent logs should contain app info");
	}

	@Test
	public void testFileHandler() {
		File tempDir = new File(System.getProperty("java.io.tmpdir"), "onelog-test");
		tempDir.mkdirs();

		OneLog.addFileHandler(tempDir, "application.log");

		Logger log = OneLog.get();
		log.info("Test file logging");

		String logFilename = OneLog.getLogFilename();
		assertNotNull(logFilename, "Log filename should not be null");
		assertTrue(new File(logFilename).exists(), "Log file should exist");

		// Clean up
		new File(logFilename).delete();
		tempDir.delete();
	}

	@Test
	public void testExceptionLogging() {
		Logger log = OneLog.get();

		try {
			throw new RuntimeException("Test exception");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception occurred", e);
		}

		String recentLogs = OneLog.getRecentLogs();
		assertTrue(recentLogs.contains("Test exception"), "Recent logs should contain exception message");
		assertTrue(recentLogs.contains("RuntimeException"), "Recent logs should contain exception type");
	}

	@Test
	public void testCircularBuffer() {
		Logger log = OneLog.get();

		// Log more than 100 messages to test circular buffer
		for (int i = 0; i < 150; i++) {
			log.info("Message " + i);
		}

		String recentLogs = OneLog.getRecentLogs();

		// Should not contain early messages (they should have been discarded)
		assertFalse(recentLogs.contains("Message 0"), "Old messages should be discarded");
		assertFalse(recentLogs.contains("Message 49"), "Old messages should be discarded");

		// Should contain recent messages
		assertTrue(recentLogs.contains("Message 149"), "Recent messages should be retained");
		assertTrue(recentLogs.contains("Message 100"), "Recent messages should be retained");
	}
}