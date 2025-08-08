package org.peakaboo.framework.scratch.encoders.compressors;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import org.junit.Test;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;

public class CompressorsTest {

	private static final String TEST_STRING = "Hello World! This is a test string for compression.";
	private static final byte[] TEST_DATA = TEST_STRING.getBytes(StandardCharsets.UTF_8);

	@Test
	public void testDeflateCompression() {
		ScratchEncoder<byte[]> compressor = Compressors.deflate();
		
		byte[] compressed = compressor.encode(TEST_DATA);
		byte[] decompressed = compressor.decode(compressed);
		
		assertArrayEquals(TEST_DATA, decompressed);
		assertEquals("Deflate Compressor", compressor.toString());
	}

	@Test
	public void testLZ4FastCompression() {
		ScratchEncoder<byte[]> compressor = Compressors.lz4fast();
		
		byte[] compressed = compressor.encode(TEST_DATA);
		byte[] decompressed = compressor.decode(compressed);
		
		assertArrayEquals(TEST_DATA, decompressed);
		assertEquals("LZ4 Fast Compressor", compressor.toString());
	}

	@Test
	public void testLZ4GoodCompression() {
		ScratchEncoder<byte[]> compressor = Compressors.lz4good();
		
		byte[] compressed = compressor.encode(TEST_DATA);
		byte[] decompressed = compressor.decode(compressed);
		
		assertArrayEquals(TEST_DATA, decompressed);
		assertEquals("LZ4 Good Compressor", compressor.toString());
	}

	@Test
	public void testNoCompression() {
		ScratchEncoder<byte[]> compressor = Compressors.none();
		
		byte[] encoded = compressor.encode(TEST_DATA);
		byte[] decoded = compressor.decode(encoded);
		
		assertArrayEquals(TEST_DATA, encoded);
		assertArrayEquals(TEST_DATA, decoded);
		assertEquals("No Compressor", compressor.toString());
	}

	@Test
	public void testEmptyData() {
		byte[] emptyData = new byte[0];
		
		for (ScratchEncoder<byte[]> compressor : getAllCompressors()) {
			byte[] compressed = compressor.encode(emptyData);
			byte[] decompressed = compressor.decode(compressed);
			assertArrayEquals("Failed for " + compressor.toString(), emptyData, decompressed);
		}
	}

	@Test
	public void testSingleByte() {
		byte[] singleByte = {42};
		
		for (ScratchEncoder<byte[]> compressor : getAllCompressors()) {
			byte[] compressed = compressor.encode(singleByte);
			byte[] decompressed = compressor.decode(compressed);
			assertArrayEquals("Failed for " + compressor.toString(), singleByte, decompressed);
		}
	}

	@Test
	public void testLargeData() {
		byte[] largeData = createLargeTestData(50000);
		
		for (ScratchEncoder<byte[]> compressor : getAllCompressors()) {
			byte[] compressed = compressor.encode(largeData);
			byte[] decompressed = compressor.decode(compressed);
			assertArrayEquals("Failed for " + compressor.toString(), largeData, decompressed);
		}
	}

	@Test
	public void testRandomData() {
		byte[] randomData = createRandomData(1000);
		
		for (ScratchEncoder<byte[]> compressor : getAllCompressors()) {
			byte[] compressed = compressor.encode(randomData);
			byte[] decompressed = compressor.decode(compressed);
			assertArrayEquals("Failed for " + compressor.toString(), randomData, decompressed);
		}
	}

	@Test
	public void testCompressionRatio() {
		String repetitiveString = createRepetitiveString(1000);
		byte[] repetitiveData = repetitiveString.getBytes(StandardCharsets.UTF_8);
		
		ScratchEncoder<byte[]> deflate = Compressors.deflate();
		ScratchEncoder<byte[]> none = Compressors.none();
		
		byte[] compressed = deflate.encode(repetitiveData);
		byte[] uncompressed = none.encode(repetitiveData);
		
		assertTrue("Compression should reduce size for repetitive data", 
				   compressed.length < uncompressed.length);
		
		assertArrayEquals(repetitiveData, deflate.decode(compressed));
		assertArrayEquals(repetitiveData, none.decode(uncompressed));
	}

	@Test
	public void testRoundTripConsistency() {
		byte[] testData = "Round trip test data".getBytes(StandardCharsets.UTF_8);
		
		for (ScratchEncoder<byte[]> compressor : getAllCompressors()) {
			byte[] firstCompress = compressor.encode(testData);
			byte[] firstDecompress = compressor.decode(firstCompress);
			
			byte[] secondCompress = compressor.encode(firstDecompress);
			byte[] secondDecompress = compressor.decode(secondCompress);
			
			assertArrayEquals("First round trip failed for " + compressor.toString(),
							  testData, firstDecompress);
			assertArrayEquals("Second round trip failed for " + compressor.toString(),
							  testData, secondDecompress);
			assertArrayEquals("Compression not consistent for " + compressor.toString(),
							  firstCompress, secondCompress);
		}
	}

	@Test(expected = ScratchException.class)
	public void testInvalidDeflateData() {
		ScratchEncoder<byte[]> compressor = Compressors.deflate();
		byte[] invalidData = {1, 2, 3, 4, 5};
		compressor.decode(invalidData);
	}

	@Test
	public void testFactoryMethodsReturnNewInstances() {
		ScratchEncoder<byte[]> deflate1 = Compressors.deflate();
		ScratchEncoder<byte[]> deflate2 = Compressors.deflate();
		assertNotSame("Factory should return new instances", deflate1, deflate2);
		
		ScratchEncoder<byte[]> lz4fast1 = Compressors.lz4fast();
		ScratchEncoder<byte[]> lz4fast2 = Compressors.lz4fast();
		assertNotSame("Factory should return new instances", lz4fast1, lz4fast2);
	}

	private ScratchEncoder<byte[]>[] getAllCompressors() {
		@SuppressWarnings("unchecked")
		ScratchEncoder<byte[]>[] compressors = new ScratchEncoder[] {
			Compressors.deflate(),
			Compressors.lz4fast(),
			Compressors.lz4good(),
			Compressors.none()
		};
		return compressors;
	}

	private byte[] createLargeTestData(int size) {
		StringBuilder sb = new StringBuilder();
		String pattern = "This is test data for compression testing. ";
		
		while (sb.length() < size) {
			sb.append(pattern);
		}
		
		return sb.substring(0, size).getBytes(StandardCharsets.UTF_8);
	}

	private byte[] createRandomData(int size) {
		byte[] data = new byte[size];
		new Random(42).nextBytes(data);
		return data;
	}

	private String createRepetitiveString(int repeats) {
		StringBuilder sb = new StringBuilder();
		String pattern = "AAABBBCCCDDDEEEFFFGGGHHHIIIJJJKKKLLLMMMNNNOOOPPPQQQRRRSSSTTTUUUVVVWWWXXXYYYZZZ";
		
		for (int i = 0; i < repeats; i++) {
			sb.append(pattern);
		}
		
		return sb.toString();
	}
}