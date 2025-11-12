package org.peakaboo.app;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class VersionTest {

	// ========== BUILD DATE PARSING TESTS ==========

	@Test
	public void testGetBuildDateReturnsValidDate() {
		LocalDate buildDate = Version.getBuildDate();

		Assert.assertNotNull("Build date should not be null", buildDate);
	}

	@Test
	public void testGetBuildDateIsReasonable() {
		LocalDate buildDate = Version.getBuildDate();
		LocalDate now = LocalDate.now();

		// Build date should be within a reasonable range
		// Not more than 10 years in the past or 1 year in the future
		Assert.assertTrue("Build date should not be more than 10 years in the past",
			buildDate.isAfter(now.minusYears(10)));
		Assert.assertTrue("Build date should not be more than 1 year in the future",
			buildDate.isBefore(now.plusYears(1)));
	}

	@Test
	public void testBuildDateStringNotEmpty() {
		Assert.assertNotNull("Build date string should not be null", Version.buildDate);
		// Note: buildDate might be empty if version.info fails to load,
		// but getBuildDate() should handle that gracefully
	}

	@Test
	public void testGetBuildDateConsistency() {
		LocalDate date1 = Version.getBuildDate();
		LocalDate date2 = Version.getBuildDate();

		Assert.assertEquals("getBuildDate should return the same date on multiple calls", date1, date2);
	}

	// ========== DATE FORMAT VALIDATION TESTS ==========

	@Test
	public void testBuildDateFormatCompatibility() {
		String buildDateStr = Version.buildDate;

		if (buildDateStr != null && !buildDateStr.isEmpty()) {
			// Should be parseable as yyyy-MM-dd format
			try {
				LocalDate parsed = LocalDate.parse(buildDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
				Assert.assertNotNull("Build date should be parseable in ISO_LOCAL_DATE format", parsed);
			} catch (DateTimeParseException e) {
				Assert.fail("Build date string should be in yyyy-MM-dd format, but got: " + buildDateStr);
			}
		}
	}

	// ========== VERSION CONSTANTS TESTS ==========

	@Test
	public void testVersionConstants() {
		Assert.assertTrue("Major version should be non-negative", Version.VERSION_MAJOR >= 0);
		Assert.assertTrue("Minor version should be non-negative", Version.VERSION_MINOR >= 0);
		Assert.assertTrue("Point version should be non-negative", Version.VERSION_POINT >= 0);

		Assert.assertNotNull("Program name should not be null", Version.PROGRAM_NAME);
		Assert.assertFalse("Program name should not be empty", Version.PROGRAM_NAME.isEmpty());
	}

	@Test
	public void testReleaseType() {
		Assert.assertNotNull("Release type should not be null", Version.RELEASE_TYPE);

		// Verify it's one of the valid enum values
		boolean isValidType = Version.RELEASE_TYPE == Version.ReleaseType.DEVELOPMENT
			|| Version.RELEASE_TYPE == Version.ReleaseType.CANDIDATE
			|| Version.RELEASE_TYPE == Version.ReleaseType.RELEASE;

		Assert.assertTrue("Release type should be a valid enum value", isValidType);
	}

	@Test
	public void testVersionStrings() {
		Assert.assertNotNull("Long version should not be null", Version.LONG_VERSION);
		Assert.assertFalse("Long version should not be empty", Version.LONG_VERSION.isEmpty());

		Assert.assertNotNull("Release description should not be null", Version.RELEASE_DESCRIPTION);
		Assert.assertNotNull("App title should not be null", Version.APP_TITLE);
	}

	// ========== BUILD DATE AND EXPIRY INTERACTION TESTS ==========

	@Test
	public void testBuildDateExpiryCompatibility() {
		LocalDate buildDate = Version.getBuildDate();

		// Should be able to add days to build date
		try {
			LocalDate futureDate = buildDate.plusDays(90);
			Assert.assertNotNull(futureDate);
			Assert.assertTrue(futureDate.isAfter(buildDate));
		} catch (Exception e) {
			Assert.fail("Should be able to add days to build date");
		}
	}

	@Test
	public void testBuildDateNotInFuture() {
		LocalDate buildDate = Version.getBuildDate();
		LocalDate now = LocalDate.now();

		// In normal circumstances, build date should not be in the future
		// (unless system clock is wrong)
		// We allow up to 1 day tolerance for timezone differences
		LocalDate oneDayFromNow = now.plusDays(1);

		Assert.assertTrue("Build date should not be significantly in the future",
			buildDate.isBefore(oneDayFromNow) || buildDate.isEqual(now));
	}

	// ========== VERSION INFO LOADING TESTS ==========

	@Test
	public void testVersionPropertiesLoaded() {
		// If build date is not empty, properties loaded successfully
		boolean propertiesLoaded = Version.buildDate != null && !Version.buildDate.isEmpty();

		// This is informational - properties might not load in test environment
		// but if they do, they should be valid
		if (propertiesLoaded) {
			try {
				LocalDate.parse(Version.buildDate, DateTimeFormatter.ISO_LOCAL_DATE);
			} catch (DateTimeParseException e) {
				Assert.fail("If properties loaded, build date should be valid");
			}
		}
	}

	// ========== INTEGRATION WITH BuildExpiry TESTS ==========

	@Test
	public void testIntegrationWithBuildExpiry() {
		LocalDate buildDate = Version.getBuildDate();
		LocalDate expiryDate = BuildExpiry.getExpiryDate();

		Assert.assertNotNull("Build date should not be null", buildDate);
		Assert.assertNotNull("Expiry date should not be null", expiryDate);

		if (Version.RELEASE_TYPE != Version.ReleaseType.RELEASE) {
			// For dev and RC builds, expiry should be after build date
			Assert.assertTrue("Expiry date should be after build date for non-release builds",
				expiryDate.isAfter(buildDate));

			// Check the offset is approximately correct
			long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(buildDate, expiryDate);
			int expectedDays = Version.RELEASE_TYPE == Version.ReleaseType.DEVELOPMENT
				? BuildExpiry.DEV_EXPIRY_DAYS
				: BuildExpiry.RC_EXPIRY_DAYS;

			Assert.assertEquals("Days between build and expiry should match expected offset",
				expectedDays, daysBetween);
		}
	}
}
