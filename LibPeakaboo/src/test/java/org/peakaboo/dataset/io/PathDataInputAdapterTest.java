package org.peakaboo.dataset.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

public class PathDataInputAdapterTest {

	// ===== addressValid =====

	@Test
	public void testAddressValidCanonicalUnix() {
		Assert.assertTrue(PathDataInputAdapter.addressValid("file:///path/to/file.dat"));
	}

	@Test
	public void testAddressValidCanonicalWindows() {
		Assert.assertTrue(PathDataInputAdapter.addressValid("file:///C:/path/to/file.dat"));
	}

	@Test
	public void testAddressValidLegacyUnixBarePath() {
		Assert.assertTrue(PathDataInputAdapter.addressValid("/path/to/file.dat"));
	}

	@Test
	public void testAddressValidLegacyWindowsBackslash() {
		Assert.assertTrue(PathDataInputAdapter.addressValid("C:\\Users\\foo\\bar.dat"));
	}

	@Test
	public void testAddressValidLegacyWindowsForwardSlash() {
		Assert.assertTrue(PathDataInputAdapter.addressValid("C:/Users/foo/bar.dat"));
	}

	@Test
	public void testAddressValidLegacyWindowsLowercaseDrive() {
		Assert.assertTrue(PathDataInputAdapter.addressValid("d:\\data\\scan.dat"));
	}

	@Test
	public void testAddressValidRejectsGibberish() {
		Assert.assertFalse(PathDataInputAdapter.addressValid("!!not-a-path"));
	}

	@Test
	public void testAddressValidRejectsRelativePath() {
		Assert.assertFalse(PathDataInputAdapter.addressValid("relative/path/file.dat"));
	}

	@Test
	public void testAddressValidRejectsTildePath() {
		Assert.assertFalse(PathDataInputAdapter.addressValid("~/path/to/file.dat"));
	}

	// ===== address() output passes addressValid =====

	@Test
	public void testAddressOutputPassesAddressValid() throws IOException {
		Path tmp = Files.createTempFile("peakaboo-test", ".dat");
		try {
			PathDataInputAdapter adapter = new PathDataInputAdapter(tmp);
			String addr = adapter.address().orElseThrow();
			Assert.assertTrue("address() output should pass addressValid()", PathDataInputAdapter.addressValid(addr));
		} finally {
			Files.deleteIfExists(tmp);
		}
	}

	@Test
	public void testAddressUsesFileUriScheme() throws IOException {
		Path tmp = Files.createTempFile("peakaboo-test", ".dat");
		try {
			PathDataInputAdapter adapter = new PathDataInputAdapter(tmp);
			String addr = adapter.address().orElseThrow();
			Assert.assertTrue("address() should produce a file: URI", addr.startsWith("file:"));
		} finally {
			Files.deleteIfExists(tmp);
		}
	}

	// ===== fromAddress round-trip =====

	@Test
	public void testFromAddressCanonicalUriRoundTrip() throws IOException {
		Path tmp = Files.createTempFile("peakaboo-test", ".dat");
		try {
			PathDataInputAdapter original = new PathDataInputAdapter(tmp);
			String addr = original.address().orElseThrow();
			PathDataInputAdapter restored = PathDataInputAdapter.fromAddress(addr, () -> null);
			Assert.assertEquals(tmp.toAbsolutePath(), restored.getAndEnsurePath().toAbsolutePath());
		} finally {
			Files.deleteIfExists(tmp);
		}
	}

	@Test
	public void testFromAddressLegacyUnixBarePath() throws IOException {
		Path tmp = Files.createTempFile("peakaboo-test", ".dat");
		try {
			String bareUnixPath = tmp.toAbsolutePath().toString();
			PathDataInputAdapter restored = PathDataInputAdapter.fromAddress(bareUnixPath, () -> null);
			Assert.assertEquals(tmp.toAbsolutePath(), restored.getAndEnsurePath().toAbsolutePath());
		} finally {
			Files.deleteIfExists(tmp);
		}
	}

	@Test
	public void testFromAddressLegacyWindowsStylePath() {
		// We can only verify addressValid + construction on Linux; the path won't exist here
		// but the adapter should be constructable without exception
		String windowsPath = "C:/Users/foo/data.dat";
		Assert.assertTrue(PathDataInputAdapter.addressValid(windowsPath));
		PathDataInputAdapter adapter = PathDataInputAdapter.fromAddress(windowsPath, () -> null);
		Assert.assertNotNull(adapter);
	}

	// ===== construct() integration via DataInputAdapters =====

	@Test
	public void testConstructAcceptsLegacyWindowsPath() {
		// construct() should not throw for a legacy Windows-style path
		DataInputAdapter adapter = DataInputAdapters.construct("C:/path/to/file.dat", () -> null);
		Assert.assertNotNull(adapter);
	}

	@Test
	public void testConstructAcceptsCanonicalFileUri() throws IOException {
		Path tmp = Files.createTempFile("peakaboo-test", ".dat");
		try {
			String uri = tmp.toUri().toString();
			DataInputAdapter adapter = DataInputAdapters.construct(uri, () -> null);
			Assert.assertNotNull(adapter);
		} finally {
			Files.deleteIfExists(tmp);
		}
	}

}
