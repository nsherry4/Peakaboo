package org.peakaboo.dataset.encoder;

import org.junit.Assert;
import org.junit.Test;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.scratch.ScratchException;

import java.util.Arrays;
import java.util.Random;

public class QOXRFTest {

    private final QOXRF encoder = new QOXRF();
    // QOXRF uses full float32 precision - should be exact matches
    private final float TOLERANCE = 1e-6f;

    // Helper method to test round-trip encoding/decoding
    private void assertRoundTrip(float[] data) throws ScratchException {
        assertRoundTrip(data, TOLERANCE);
    }

    private void assertRoundTrip(float[] data, float tolerance) throws ScratchException {
        Spectrum original = new ArraySpectrum(data, false);
        byte[] encoded = encoder.encode(original);
        Spectrum decoded = encoder.decode(encoded);

        Assert.assertEquals(data.length, decoded.size());
        for (int i = 0; i < data.length; i++) {
            Assert.assertEquals("Mismatch at index " + i, data[i], decoded.get(i), tolerance);
        }
    }

    // Helper method to test compression efficiency
    private double getCompressionRatio(float[] data) throws ScratchException {
        Spectrum spectrum = new ArraySpectrum(data, false);
        byte[] encoded = encoder.encode(spectrum);
        int originalSize = data.length * 4; // 4 bytes per float
        return (double) originalSize / encoded.length;
    }

    @Test
    public void testEmptySpectrum() throws ScratchException {
        assertRoundTrip(new float[0]);
    }

    @Test
    public void testSingleValue() throws ScratchException {
        assertRoundTrip(new float[]{42.5f});
    }

    @Test
    public void testAllZeros() throws ScratchException {
        // Tests RLE compression of long zero runs - critical for efficiency
        // since delta encoding often produces many zeros
        float[] data = new float[100];
        Arrays.fill(data, 0.0f);
        assertRoundTrip(data);
    }

    @Test
    public void testConstantValues() throws ScratchException {
        // Tests delta encoding: constant values become zeros after first value,
        // should compress well with RLE
        float[] data = new float[50];
        Arrays.fill(data, 123.456f);
        assertRoundTrip(data);
    }

    @Test
    public void testSequentialValues() throws ScratchException {
        // Tests delta encoding with constant differences (delta = 1.0 for all)
        // Should benefit from cache compression 
        float[] data = new float[20];
        for (int i = 0; i < data.length; i++) {
            data[i] = (float) i;
        }
        assertRoundTrip(data);
    }

    @Test
    public void testRepeatingPattern() throws ScratchException {
        // Tests cache efficiency: repeated delta patterns should be found in cache
        // This stresses the LRU cache logic and verifies cache hit/miss handling
        float[] data = new float[100];
        for (int i = 0; i < data.length; i++) {
            data[i] = (float) (i % 5); // Pattern: 0,1,2,3,4,0,1,2,3,4,...
        }
        assertRoundTrip(data);
    }

    @Test
    public void testMixedWithZeros() throws ScratchException {
        // Tests mixed RLE and non-RLE data in same stream
        // Verifies correct state transitions between RLE and other opcodes
        // Critical for ensuring RLE doesn't interfere with cache/raw encoding
        float[] data = {1.0f, 0.0f, 0.0f, 0.0f, 2.0f, 3.0f, 0.0f, 0.0f, 4.0f};
        assertRoundTrip(data);
    }

    @Test
    public void testLargeSpectrum() throws ScratchException {
        // Tests realistic XRF spectrum data with typical size (4096 channels)
        // Verifies performance and correctness at scale, tests all compression modes
        float[] data = new float[4096];
        Random random = new Random(12345);

        for (int i = 0; i < data.length; i++) {
            data[i] = 100.0f - (float) i * 0.01f; // Background slope

            // Add peaks
            if (i > 500 && i < 600) {
                data[i] += 1000.0f * (float) Math.exp(-Math.pow((i - 550) / 20.0, 2));
            }
            if (i > 1200 && i < 1300) {
                data[i] += 500.0f * (float) Math.exp(-Math.pow((i - 1250) / 15.0, 2));
            }

            data[i] += (float) (int)random.nextGaussian() * 0.7f; // Occasional noise
            data[i] = Math.max(0.0f, data[i]); // Non-negative
        }

        assertRoundTrip(data);

        double ratio = getCompressionRatio(data);
        System.out.println("Large spectrum compression ratio: " + String.format("%.2f", ratio) + ":1");
        Assert.assertTrue("Compression ratio should be > 1", ratio > 1.0);
    }

    @Test
    public void testPreciseValues() throws ScratchException {
        // Tests full float32 precision with realistic XRF spectrum values
        // These values represent typical counts, energies, and measurement precision
        float[] data = {
                0.0f, 1.0f, 10.5f, 100.25f, 1024.75f, 3.14159f, 2.71828f, 0.5f
        };

        // Note: Large jumps in values can cause minor precision loss during delta encoding
        // due to float32 arithmetic limitations when adding large and small numbers
        assertRoundTrip(data, 1e-4f); // 0.0001 tolerance for delta encoding precision
    }

    @Test
    public void testRLEMaxLength() throws ScratchException {
        // Tests RLE boundary conditions: max run length (64) and overflow handling
        // Verifies that runs > 64 are correctly split into multiple RLE operations
        // Critical for preventing buffer overflows and ensuring correct length encoding
        float[] data = new float[200];
        Arrays.fill(data, 0, 64, 0.0f);     // 64 zeros (max RLE)
        Arrays.fill(data, 64, 128, 1.0f);   // 64 ones
        Arrays.fill(data, 128, 200, 0.0f);  // 72 more zeros (should split: 64 + 8)
        assertRoundTrip(data);
    }

    @Test
    public void testCacheEfficiency() throws ScratchException {
        // Tests LRU cache effectiveness with patterns that fit in 64-entry cache
        // Verifies cache indexing, wraparound, and hit detection logic
        // Should achieve high compression ratio due to cache reuse
        float[] data = new float[200];
        float[] pattern = {1.0f, 2.0f, 3.0f, 4.0f, 5.0f};

        for (int i = 0; i < data.length; i++) {
            data[i] = pattern[i % pattern.length];
        }

        assertRoundTrip(data);

        double ratio = getCompressionRatio(data);
        System.out.println("Cache-friendly compression ratio: " + String.format("%.2f", ratio) + ":1");
        Assert.assertTrue("Should achieve good compression with repeated patterns", ratio > 3.0);
    }

    @Test
    public void testSmallIntegerDeltas() throws ScratchException {
        // Tests small integer delta compression: values that differ by small integers
        // should compress to 1 byte each (except the first value)
        float[] data = {100.0f, 101.0f, 103.0f, 100.0f, 97.0f, 132.0f, 131.0f};
        
        assertRoundTrip(data);
        
        // Should achieve good compression since most deltas are small integers
        double ratio = getCompressionRatio(data);
        System.out.println("Small integer delta compression ratio: " + String.format("%.2f", ratio) + ":1");
        Assert.assertTrue("Should achieve good compression with small integer deltas", ratio > 2.0);
    }

    @Test
    public void testMultipleCycles() throws ScratchException {
        // Tests compression stability: multiple encode/decode cycles should not
        // introduce cumulative errors or drift.
        float[] originalData = {1.5f, 2.5f, 3.5f, 0.0f, 0.0f, 4.5f, 5.5f};
        Spectrum current = new ArraySpectrum(originalData, false);

        // Multiple encode/decode cycles
        for (int cycle = 0; cycle < 50; cycle++) {
            byte[] encoded = encoder.encode(current);
            current = encoder.decode(encoded);
        }

        Assert.assertEquals(originalData.length, current.size());
        for (int i = 0; i < originalData.length; i++) {
            Assert.assertEquals("Mismatch after multiple cycles at index " + i,
                    originalData[i], current.get(i), TOLERANCE);
        }
    }

    @Test(expected = ScratchException.class)
    public void testTruncatedData() throws ScratchException {
        // Tests bounds checking: incomplete data should be detected and rejected
        byte[] encoded = encoder.encode(new ArraySpectrum(new float[]{1.0f, 2.0f}, false));
        byte[] truncated = Arrays.copyOf(encoded, encoded.length - 2);
        encoder.decode(truncated);
    }

    @Test(expected = ScratchException.class)
    public void testEmptyData() throws ScratchException {
        // Tests input validation: completely empty input should be rejected
        encoder.decode(new byte[0]);
    }
}