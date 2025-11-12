package org.peakaboo.app;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.peakaboo.app.BuildExpiry.TimeResult;

public class BuildExpiryTest {

	// ========== EXPIRY DATE CALCULATION TESTS ==========

	@Test
	public void testExpiryDateForDevelopmentBuild() {
		// This test assumes Version.RELEASE_TYPE is DEVELOPMENT
		// and that Version.getBuildDate() returns a valid date
		if (Version.RELEASE_TYPE != Version.ReleaseType.DEVELOPMENT) {
			return; // Skip if not a dev build
		}

		LocalDate buildDate = Version.getBuildDate();
		LocalDate expiryDate = BuildExpiry.getExpiryDate();
		LocalDate expectedExpiry = buildDate.plusDays(BuildExpiry.DEV_EXPIRY_DAYS);

		Assert.assertEquals("Development build expiry date should be build date + " + BuildExpiry.DEV_EXPIRY_DAYS + " days",
			expectedExpiry, expiryDate);
	}

	@Test
	public void testExpiryConstants() {
		Assert.assertTrue("Dev expiry days should be positive", BuildExpiry.DEV_EXPIRY_DAYS > 0);
		Assert.assertTrue("RC expiry days should be positive", BuildExpiry.RC_EXPIRY_DAYS > 0);
		Assert.assertTrue("Dev builds should have longer expiry than RC builds",
			BuildExpiry.DEV_EXPIRY_DAYS > BuildExpiry.RC_EXPIRY_DAYS);
		Assert.assertTrue("Dev builds should expire after at least 30 days", BuildExpiry.DEV_EXPIRY_DAYS >= 30);
		Assert.assertTrue("RC builds should expire after at least 7 days", BuildExpiry.RC_EXPIRY_DAYS >= 7);
	}

	// ========== TIME RESULT TESTS ==========

	@Test
	public void testTimeResult() {
		LocalDateTime testTime = LocalDateTime.of(2025, 1, 15, 10, 30);
		String testSource = "http://test.api.com";

		TimeResult result = new TimeResult(testTime, testSource, true);

		Assert.assertEquals(testTime, result.currentTime);
		Assert.assertEquals(testSource, result.source);
		Assert.assertTrue(result.isInternetTime);
	}

	@Test
	public void testTimeResultLocalVsInternet() {
		LocalDateTime testTime = LocalDateTime.now();

		TimeResult internetTime = new TimeResult(testTime, "http://api.example.com", true);
		TimeResult localTime = new TimeResult(testTime, "local system time", false);

		Assert.assertTrue(internetTime.isInternetTime);
		Assert.assertFalse(localTime.isInternetTime);
	}

	// ========== INTEGRATION TESTS ==========

	@Test
	public void testGetCurrentTimeReturnsValidResult() {
		// This is an integration test that actually calls the network
		// It might fail if network is unavailable, but tests the real implementation
		try {
			TimeResult result = BuildExpiry.getCurrentTime();

			Assert.assertNotNull("TimeResult should not be null", result);
			Assert.assertNotNull("Current time should not be null", result.currentTime);
			Assert.assertNotNull("Time source should not be null", result.source);
			Assert.assertFalse("Time source should not be empty", result.source.isEmpty());

			// Sanity check: time should be reasonable (within 10 years of now)
			LocalDateTime now = LocalDateTime.now();
			Assert.assertTrue("Retrieved time should be within reasonable range",
				result.currentTime.isAfter(now.minusYears(10)));
			Assert.assertTrue("Retrieved time should be within reasonable range",
				result.currentTime.isBefore(now.plusYears(10)));

		} catch (Exception e) {
			// If network is unavailable, the test should gracefully handle it
			// The actual implementation falls back to local time
			Assert.assertNotNull("Exception should have a message", e.getMessage());
		}
	}

	@Test
	public void testTimeApiConfiguration() {
		// Verify the constants are set up correctly
		Assert.assertNotNull("BuildExpiry class should exist", BuildExpiry.class);

		// These are basic structural tests
		Assert.assertEquals("Dev expiry should be 90 days", 90, BuildExpiry.DEV_EXPIRY_DAYS);
		Assert.assertEquals("RC expiry should be 30 days", 30, BuildExpiry.RC_EXPIRY_DAYS);
	}
}
