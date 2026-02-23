package org.peakaboo.dataset.io;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for DataInputAdapters, specifically verifying that unrecognised file paths
 * throw IllegalArgumentException instead of returning null (which would cause NPE downstream).
 */
public class DataInputAdaptersTest {

	@Test(expected = IllegalArgumentException.class)
	public void testConstructWithUnrecognisedPath() {
		// This path format is not recognised by any registered handler
		// (neither PathDataInputAdapter nor URLDataInputAdapter)
		DataInputAdapters.construct("!!invalid://path#@$", () -> null);
	}

	@Test
	public void testConstructWithValidFilePath() {
		// Valid file path should construct a PathDataInputAdapter
		DataInputAdapter adapter = DataInputAdapters.construct("/tmp/test.dat", () -> null);
		Assert.assertNotNull("construct() should return non-null for valid file path", adapter);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromFilenamesWithInvalidPath() {
		// When fromFilenames() encounters an invalid path, it should propagate the exception
		List<String> filenames = Arrays.asList("/valid/path.dat", "!!invalid://path");
		DataInputAdapter.fromFilenames(filenames);
	}

	@Test
	public void testExceptionMessage() {
		// Verify that the exception message is informative
		try {
			DataInputAdapters.construct("@@unrecognised@@", () -> null);
			Assert.fail("Expected IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			String message = e.getMessage();
			Assert.assertNotNull("Exception message should not be null", message);
			Assert.assertTrue("Exception message should mention the path",
					message.contains("@@unrecognised@@"));
			Assert.assertTrue("Exception message should indicate no handler was found",
					message.toLowerCase().contains("handler") || message.toLowerCase().contains("registered"));
		}
	}

	// ===== URL Protocol Support =====
	// Tests verifying that both file paths and URLs (HTTP/HTTPS) are correctly
	// handled by the adapter construction system

	@Test
	public void testConstructWithValidURL() {
		// HTTP URLs should construct a URLDataInputAdapter
		DataInputAdapter adapter = DataInputAdapters.construct("http://example.com/data.dat", () -> null);
		Assert.assertNotNull("construct() should return non-null for valid HTTP URL", adapter);
		Assert.assertTrue("HTTP URL should construct URLDataInputAdapter",
				adapter instanceof URLDataInputAdapter);
	}

	@Test
	public void testConstructWithHTTPSURL() {
		// HTTPS URLs should also work
		DataInputAdapter adapter = DataInputAdapters.construct("https://example.com/data.dat", () -> null);
		Assert.assertNotNull("construct() should return non-null for valid HTTPS URL", adapter);
		Assert.assertTrue("HTTPS URL should construct URLDataInputAdapter",
				adapter instanceof URLDataInputAdapter);
	}

	@Test
	public void testFromFilenamesWithMixedFileAndURL() {
		// Mixed file and URL paths should all construct correctly
		List<String> mixed = Arrays.asList(
			"/local/file.dat",
			"http://example.com/remote.dat",
			"/another/local.dat"
		);
		List<DataInputAdapter> adapters = DataInputAdapter.fromFilenames(mixed);

		Assert.assertEquals("Should return 3 adapters", 3, adapters.size());
		Assert.assertNotNull("First adapter should not be null", adapters.get(0));
		Assert.assertNotNull("Second adapter should not be null", adapters.get(1));
		Assert.assertNotNull("Third adapter should not be null", adapters.get(2));

		// Verify types
		Assert.assertTrue("First should be PathDataInputAdapter",
				adapters.get(0) instanceof PathDataInputAdapter);
		Assert.assertTrue("Second should be URLDataInputAdapter",
				adapters.get(1) instanceof URLDataInputAdapter);
		Assert.assertTrue("Third should be PathDataInputAdapter",
				adapters.get(2) instanceof PathDataInputAdapter);
	}

	// ===== Edge Cases and Boundary Conditions =====
	// Tests verifying behaviour with empty lists, all-invalid paths, and invalid
	// paths at different positions in the list

	@Test
	public void testFromFilenamesWithEmptyList() {
		// Empty list should return empty list, not crash
		List<DataInputAdapter> adapters = DataInputAdapter.fromFilenames(Collections.emptyList());
		Assert.assertNotNull("Should return non-null list", adapters);
		Assert.assertTrue("Should return empty list", adapters.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromFilenamesWithAllInvalidPaths() {
		// All invalid paths should throw exception on first invalid path
		List<String> allInvalid = Arrays.asList("!!invalid1", "@@invalid2", "##invalid3");
		DataInputAdapter.fromFilenames(allInvalid);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromFilenamesWithInvalidPathFirst() {
		// Invalid path at the start should throw exception
		DataInputAdapter.fromFilenames(Arrays.asList("!!invalid", "/valid/path.dat"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromFilenamesWithInvalidPathMiddle() {
		// Invalid path in the middle should throw exception
		DataInputAdapter.fromFilenames(Arrays.asList("/valid1.dat", "!!invalid", "/valid2.dat"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromFilenamesWithInvalidPathLast() {
		// Invalid path at the end should throw exception
		DataInputAdapter.fromFilenames(Arrays.asList("/valid/path.dat", "!!invalid"));
	}
}
