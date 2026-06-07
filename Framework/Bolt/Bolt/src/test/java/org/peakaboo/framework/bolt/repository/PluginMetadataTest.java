package org.peakaboo.framework.bolt.repository;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PluginMetadataTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void checksum_validFile_returnsHex() throws Exception {
		File f = tempFolder.newFile("testfile.jar");
		Files.writeString(f.toPath(), "hello world");
		String result = PluginMetadata.checksum(f.getAbsolutePath());
		assertNotNull(result);
		assertTrue("Expected 32 hex chars, got: " + result, result.matches("[0-9a-fA-F]{32}"));
	}

	@Test
	public void normalizeChecksum_leftPadsToThirtyTwoChars() {
		String result = PluginMetadata.normalizeChecksum("abc");
		assertEquals(32, result.length());
		assertTrue("Should preserve the original hex as the low-order digits", result.endsWith("abc"));
		assertTrue("Padding should be leading zeros", result.startsWith("00000000000000000000000000000"));
		// An already-canonical checksum is returned unchanged
		String canonical = "d41d8cd98f00b204e9800998ecf8427e";
		assertEquals(canonical, PluginMetadata.normalizeChecksum(canonical));
	}

	@Test
	public void normalizeChecksum_rejectsInvalid() {
		assertNull(PluginMetadata.normalizeChecksum(null));
		assertNull(PluginMetadata.normalizeChecksum(""));
		assertNull("Non-hex should be rejected", PluginMetadata.normalizeChecksum("None"));
		assertNull("Over-length should be rejected", PluginMetadata.normalizeChecksum("0".repeat(33)));
	}

	@Test
	public void validateChecksum_legacyUnpaddedStored_returnsTrue() throws Exception {
		// Find file content whose MD5 has at least one leading zero nibble, so the
		// canonical (padded) checksum genuinely differs from the legacy stripped form.
		// Roughly 1/16 of inputs qualify, so this terminates quickly.
		File f = tempFolder.newFile("legacy.jar");
		String canonical;
		int attempt = 0;
		do {
			Files.writeString(f.toPath(), "legacy content " + attempt++);
			canonical = PluginMetadata.checksum(f.getAbsolutePath());
		} while (canonical.charAt(0) != '0');

		String stripped = canonical.replaceFirst("^0+", "");
		assertNotEquals("Test must exercise a genuinely stripped checksum", canonical, stripped);

		PluginMetadata meta = new PluginMetadata();
		meta.checksum = stripped; // simulate an older inventory with leading zeros stripped
		assertTrue(meta.validateChecksum(f.getAbsolutePath()));
	}

	@Test
	public void checksum_nonexistentFile_returnsNull() {
		String result = PluginMetadata.checksum("/nonexistent/path/file.jar");
		assertNull(result);
	}

	@Test
	public void validateChecksum_matching_returnsTrue() throws Exception {
		File f = tempFolder.newFile("checksumtest.jar");
		Files.writeString(f.toPath(), "test content for checksum");
		PluginMetadata meta = new PluginMetadata();
		meta.checksum = PluginMetadata.checksum(f.getAbsolutePath());
		assertNotNull(meta.checksum);
		assertTrue(meta.validateChecksum(f.getAbsolutePath()));
	}

	@Test
	public void validateChecksum_mismatched_returnsFalse() throws Exception {
		File f = tempFolder.newFile("mismatch.jar");
		Files.writeString(f.toPath(), "some content");
		PluginMetadata meta = new PluginMetadata();
		meta.checksum = "00000000000000000000000000000000";
		assertFalse(meta.validateChecksum(f.getAbsolutePath()));
	}

	/**
	 * Bug: line 171 calls {@code this.checksum.equalsIgnoreCase(md5sum)} but
	 * this.checksum is null when not set, causing a NullPointerException.
	 * Should return false when checksum has not been set.
	 */
	@Test
	public void validateChecksum_nullStoredChecksum_returnsFalse() throws Exception {
		File f = tempFolder.newFile("nullcheck.jar");
		Files.writeString(f.toPath(), "content");
		PluginMetadata meta = new PluginMetadata();
		// meta.checksum is null by default
		assertFalse("validateChecksum should return false when stored checksum is null", meta.validateChecksum(f.getAbsolutePath()));
	}

	@Test
	public void equals_sameFields_returnsTrue() {
		PluginMetadata a = buildMeta();
		PluginMetadata b = buildMeta();
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}

	/**
	 * Bug: line 252 calls {@code this.uuid.equals(other.uuid)} but uuid is null
	 * when not set, causing a NullPointerException. A PluginMetadata with a null
	 * UUID has no identity and should not be equal to anything other than itself.
	 */
	@Test
	public void equals_nullUuid_returnsFalse() {
		PluginMetadata a = new PluginMetadata();
		PluginMetadata b = new PluginMetadata();
		assertNotEquals("PluginMetadata with null UUID should not equal another", a, b);
	}

	private static PluginMetadata buildMeta() {
		PluginMetadata p = new PluginMetadata();
		p.uuid = "12345678-1234-1234-1234-123456789abc";
		p.name = "Test Plugin";
		p.repositoryUrl = "https://example.com/repo";
		p.downloadUrl = "https://example.com/repo/plugin.jar";
		return p;
	}

}
