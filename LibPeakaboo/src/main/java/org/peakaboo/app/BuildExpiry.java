package org.peakaboo.app;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;

import org.peakaboo.app.Version.ReleaseType;
import org.peakaboo.framework.accent.log.OneLog;

/**
 * Handles build expiry checking for development and release candidate builds.
 * Fetches current time from internet sources with fallback to local system time.
 */
public class BuildExpiry {

	public static final int DEV_EXPIRY_DAYS = 90;
	public static final int RC_EXPIRY_DAYS = 30;

	/**
	 * Environment variable to disable build expiry checks.
	 * Set to "true" or "1" to disable expiry checks for development purposes.
	 */
	public static final String ENV_DISABLE_EXPIRY = "PEAKABOO_DISABLE_EXPIRY";

	private static final String[] TIME_API_URLS = {
		"https://timeapi.io/api/v1/time/current/zone?timezone=UTC",
		"https://www.cloudflare.com",
		"https://www.google.com"
	};

	private static final Duration TIMEOUT = Duration.ofSeconds(5);

	/**
	 * Result of fetching current time, including the time and source information
	 */
	public static class TimeResult {
		public final LocalDateTime currentTime;
		public final String source;
		public final boolean isInternetTime;

		public TimeResult(LocalDateTime currentTime, String source, boolean isInternetTime) {
			this.currentTime = currentTime;
			this.source = source;
			this.isInternetTime = isInternetTime;
		}
	}

	/**
	 * Gets the current date/time from internet sources, falling back to local system time
	 */
	public static TimeResult getCurrentTime() {
		HttpClient client = HttpClient.newBuilder()
			.connectTimeout(TIMEOUT)
			.build();

		// Try each internet time source
		for (String apiUrl : TIME_API_URLS) {
			try {
				LocalDateTime time = fetchTimeFromApi(client, apiUrl);
				if (time != null) {
					OneLog.log(Level.INFO, "Fetched time from internet source: " + apiUrl);
					return new TimeResult(time, apiUrl, true);
				}
			} catch (Exception e) {
				OneLog.log(Level.WARNING, "Failed to fetch time from " + apiUrl + ": " + e.getMessage());
			}
		}

		// Fall back to local system time
		OneLog.log(Level.WARNING, "Could not fetch time from internet, using local system time");
		LocalDateTime localTime = LocalDateTime.now();
		return new TimeResult(localTime, "local system time", false);
	}

	/**
	 * Fetches current time from a time API
	 */
	private static LocalDateTime fetchTimeFromApi(HttpClient client, String apiUrlString) throws IOException, InterruptedException {
		// timeapi.io needs a full response body to parse; the other sources are
		// only used for their standard HTTP Date response header, so HEAD suffices
		boolean isTimeApiIo = apiUrlString.contains("timeapi.io");

		HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
			.uri(URI.create(apiUrlString))
			.timeout(TIMEOUT)
			.header("User-Agent", "Peakaboo/" + Version.LONG_VERSION);
		requestBuilder = isTimeApiIo ? requestBuilder.GET() : requestBuilder.method("HEAD", HttpRequest.BodyPublishers.noBody());
		HttpRequest request = requestBuilder.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() != 200) {
			throw new IOException("HTTP response code: " + response.statusCode());
		}

		if (isTimeApiIo) {
			return parseTimeApiIo(response.body());
		}

		String dateHeader = response.headers().firstValue("Date")
			.orElseThrow(() -> new IOException("No Date header in response"));
		return parseHttpDateHeader(dateHeader);
	}

	/**
	 * Parses a standard HTTP Date response header
	 * Example: Tue, 15 Jan 2025 10:30:45 GMT
	 */
	private static LocalDateTime parseHttpDateHeader(String dateHeader) {
		ZonedDateTime zdt = ZonedDateTime.parse(dateHeader, DateTimeFormatter.RFC_1123_DATE_TIME);
		return zdt.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
	}

	/**
	 * Parses TimeAPI.io JSON response
	 * Example: {"date_time":"2025-01-15T10:30:45.123456+00:00",...}
	 */
	private static LocalDateTime parseTimeApiIo(String json) {
		// Simple JSON parsing for date_time field
		String dateTimeKey = "\"date_time\":\"";
		int startIdx = json.indexOf(dateTimeKey);
		if (startIdx == -1) {
			throw new IllegalArgumentException("Could not find date_time field");
		}
		startIdx += dateTimeKey.length();
		int endIdx = json.indexOf("\"", startIdx);
		String dateTimeStr = json.substring(startIdx, endIdx);

		// The v1 API includes a UTC offset (e.g. +00:00), so parse with the
		// offset and normalize to UTC rather than assuming a local time
		ZonedDateTime zdt = ZonedDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		return zdt.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
	}

	/**
	 * Calculates the expiry date based on build date and release type
	 */
	public static LocalDate getExpiryDate() {
		LocalDate buildDate = Version.getBuildDate();
		int expiryDays;

		switch (Version.RELEASE_TYPE) {
		case DEVELOPMENT:
			expiryDays = DEV_EXPIRY_DAYS;
			break;
		case CANDIDATE:
			expiryDays = RC_EXPIRY_DAYS;
			break;
		case RELEASE:
		default:
			// Release builds don't expire, return far future date
			return LocalDate.of(9999, 12, 31);
		}

		return buildDate.plusDays(expiryDays);
	}

	/**
	 * Checks if the current build has expired
	 */
	public static boolean isExpired(TimeResult currentTime) {
		// Check for environment variable override
		String disableExpiry = System.getenv(ENV_DISABLE_EXPIRY);
		if (disableExpiry != null && (disableExpiry.equalsIgnoreCase("true") || disableExpiry.equals("1"))) {
			OneLog.log(Level.INFO, "Build expiry check disabled by " + ENV_DISABLE_EXPIRY + " environment variable");
			return false;
		}

		if (Version.RELEASE_TYPE == ReleaseType.RELEASE) {
			return false;
		}
		LocalDate expiryDate = getExpiryDate();
		LocalDate currentDate = currentTime.currentTime.toLocalDate();
		return currentDate.isAfter(expiryDate);
	}

	/**
	 * Gets the number of days remaining until expiry (negative if expired)
	 */
	public static long getDaysRemaining(TimeResult currentTime) {
		if (Version.RELEASE_TYPE == ReleaseType.RELEASE) {
			return Long.MAX_VALUE;
		}
		LocalDate expiryDate = getExpiryDate();
		LocalDate currentDate = currentTime.currentTime.toLocalDate();
		return ChronoUnit.DAYS.between(currentDate, expiryDate);
	}
}