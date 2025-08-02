package org.peakaboo.dataset.encoder;

import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Inspired by the QOI image format (https://qoiformat.org/), this is the QOXRF
 * or Quite Okay XRF format. It uses delta encoding plus simple compression
 * methods like:
 * <ul>
 * <li>Run Length Encoding (RLE) for delta-encoded zeros (meaning no change in
 * signal value from the previous channel) are packed as 1 byte.</li>
 * <li>Small integer(ish) changes from the previous value are packed as 1 byte.
 * </li>
 * <li>Values repeated from recent channels are packed as 1 byte.</li>
 * </ul>
 * <br>
 * Two-bit opcodes leave 6 bits for length of run, value of small integer
 * offset, or recent value cache index. This is enough that we can pack most
 * channel values into a single byte. The raw, uncompressable float values are
 * packed into four dedicated bytes.<br><br>
 *
 * In several test datasets, this compression algorithm outperforms a native
 * implementation of LZ4 in both speed and compression size for this specific
 * type of data.<br><br>
 *
 * There is an opportunity to shrink the file size even more by discarding the
 * two least significant bits of the mantissa and packing the opcode and float
 * into 4 bytes together, but this would be slightly lossy and has been avoided
 * for now.<br><br>
 *
 * While most of the data sets we see have integer count values, there are data
 * sets which are expressed as floats, and may have values *near* zero but, due
 * to noise, not close enough for RLE compression and too random for a cache
 * reference. Particularly low noise levels may get rounded off by the small
 * integer(ish) delta opcode, but stronger noise won't be coerced to an integer.
 * In those cases, rounding off values near zero *before* delta encoding may
 * improve compression on float data sets without a significant loss in
 * precision.
 */

public class QOXRF implements ScratchEncoder<Spectrum> {
    
    private static final int OP_RLE_ZERO = 0;        // 00
    private static final int OP_CACHE_REF = 1;       // 01
    private static final int OP_SMALL_INT_DELTA = 2; // 10
    private static final int OP_RAW_VALUE = 3;       // 11

    
    private static final int CACHE_SIZE = 64;
    private static final int MAX_RLE_LENGTH = 64;

    @Override
    public byte[] encode(Spectrum data) throws ScratchException {
        float[] floats = data.backingArray();
        float[] deltas = new float[floats.length];
        
        // Delta encoding: first value as-is, subsequent as differences
        if (floats.length > 0) {
            deltas[0] = floats[0];
            for (int i = 1; i < floats.length; i++) {
                deltas[i] = floats[i] - floats[i - 1];
            }
        }
        
        // Convert deltas to int representation (raw float bits)
        int[] floatBits = new int[deltas.length];
        for (int i = 0; i < deltas.length; i++) {
            floatBits[i] = Float.floatToIntBits(deltas[i]);
        }
        
        byte[] compressed = compressValues(floatBits);
        
        // Create 4-byte header: just the length as uint32
        byte[] result = new byte[4 + compressed.length];
        result[0] = (byte) (floatBits.length & 0xFF);         // Length byte 0 (LSB)
        result[1] = (byte) ((floatBits.length >> 8) & 0xFF);  // Length byte 1
        result[2] = (byte) ((floatBits.length >> 16) & 0xFF); // Length byte 2
        result[3] = (byte) ((floatBits.length >> 24) & 0xFF); // Length byte 3 (MSB)
        
        System.arraycopy(compressed, 0, result, 4, compressed.length);
        
        return result;
    }
    
    private byte[] compressValues(int[] values) {
        // Estimate max size: worst case is all raw values (5 bytes each)
        ByteBuffer compressed = ByteBuffer.allocate(values.length * 5);
        int[] cache = new int[CACHE_SIZE];
        int cacheIndex = 0;

        int i = 0;
        while (i < values.length) {
            int current = values[i];

            // Check for zero run-length encoding
            if (current == 0) {
                int runLength = 1;
                int maxLength = Math.min(values.length - i, MAX_RLE_LENGTH);
                while (runLength < maxLength && values[i + runLength] == 0) {
                    runLength++;
                }
                // Encode: 00xxxxxx (RLE_ZERO with 6-bit length) - 1 byte total
                compressed.put((byte) ((OP_RLE_ZERO << 6) | (runLength - 1)));
                i += runLength;
                continue;
            }

            // Check for small integer delta: -31 to +32
            float currentFloat = Float.intBitsToFloat(current);
            int rounded = Math.round(currentFloat);
            if (rounded >= -31 && rounded <= 32 && Math.abs(currentFloat - rounded) < 1e-7f) {
                // Encode: 10xxxxxx (SMALL_INT_DELTA with 6-bit signed offset)
                // Map -31..+32 to 0..63 by adding 31
                int encoded = rounded + 31;
                compressed.put((byte) ((OP_SMALL_INT_DELTA << 6) | encoded));
                i++;
                continue;
            }

            // Check cache for recent values
            int cacheOffset = findInCache(cache, cacheIndex, current);
            if (cacheOffset != -1) {
                // Encode: 01xxxxxx (CACHE_REF with 6-bit offset) - 1 byte total
                compressed.put((byte) ((OP_CACHE_REF << 6) | cacheOffset));
                
                // Add to cache (maintain same order as decoder)
                cache[cacheIndex] = current;
                cacheIndex = (cacheIndex + 1) % CACHE_SIZE;
                i++;
                continue;
            }

            // Raw value encoding: 1 byte opcode + 4 bytes full float32 (no precision loss)
            compressed.put((byte) (OP_RAW_VALUE << 6)); // 6 padding bits are 0
            compressed.put((byte) (current & 0xFF));         // Byte 0 (LSB)
            compressed.put((byte) ((current >> 8) & 0xFF));  // Byte 1
            compressed.put((byte) ((current >> 16) & 0xFF)); // Byte 2
            compressed.put((byte) ((current >> 24) & 0xFF)); // Byte 3 (MSB)
            
            // Store the original value in cache
            cache[cacheIndex] = current;
            cacheIndex = (cacheIndex + 1) % CACHE_SIZE;
            
            i++;
        }

        if (i != values.length) {
            throw new IllegalStateException("Encoder bug: expected to encode " + values.length + " values, but encoded " + i);
        }
        
        // Return only the used portion
        byte[] result = new byte[compressed.position()];
        compressed.flip();
        compressed.get(result);
        return result;
    }
    
    private int findInCache(int[] cache, int cacheIndex, int value) {
        // Search from most recent to oldest - this takes advantage of temporal locality
        // where recently used values are more likely to be accessed again.
        // The modulo arithmetic ensures we wrap around the circular buffer correctly.
        for (int i = 0; i < CACHE_SIZE; i++) {
            int index = (cacheIndex - 1 - i + CACHE_SIZE) % CACHE_SIZE;
            if (cache[index] == value) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Spectrum decode(byte[] data) throws ScratchException {
        return new ArraySpectrum(decompressAndDecode(data), false);
    }
    
    private float[] decompressAndDecode(byte[] data) throws ScratchException {
        if (data.length < 4) {
            throw new ScratchException(new IllegalArgumentException("Data too short for header"));
        }
        
        // Read header - 4 bytes for length
        int expectedLength = (data[0] & 0xFF) |
                           ((data[1] & 0xFF) << 8) |
                           ((data[2] & 0xFF) << 16) |
                           ((data[3] & 0xFF) << 24);
        
        // Single allocation: final float array for delta-decoded values
        float[] floats = new float[expectedLength];
        int floatsIndex = 0;
        int[] cache = new int[CACHE_SIZE];
        int cacheIndex = 0;
        float previousValue = 0.0f; // For delta decoding
        
        int i = 4; // Start after header
        while (i < data.length && floatsIndex < expectedLength) {

            // Read first byte to determine opcode
            int firstByte = data[i] & 0xFF;
            int opcode = (firstByte >>> 6) & 0x3;

            // Now process the opcode safely
            switch (opcode) {
                case OP_RLE_ZERO:
                    int runLength = (firstByte & 0x3F) + 1;
                    int endIndex = Math.min(floatsIndex + runLength, expectedLength);
                    Arrays.fill(floats, floatsIndex, endIndex, previousValue);
                    floatsIndex = endIndex;
                    i++; // Advance by 1 byte
                    break;

                case OP_SMALL_INT_DELTA:
                    // Decode small integer delta: 6 bits encode -31..+32, map 0..63 back to -31..+32
                    previousValue += (float) ((firstByte & 0x3F) - 31);
                    floats[floatsIndex++] = previousValue;
                    i++; // Advance by 1 byte
                    break;

                case OP_CACHE_REF:
                    int cachePos = (cacheIndex - 1 - (firstByte & 0x3F) + CACHE_SIZE) % CACHE_SIZE;
                    
                    // Convert int bits back to float delta and apply delta decoding
                    previousValue += Float.intBitsToFloat(cache[cachePos]);
                    floats[floatsIndex++] = previousValue;
                    
                    cache[cacheIndex] = cache[cachePos];
                    cacheIndex = (cacheIndex + 1) % CACHE_SIZE;
                    i++; // Advance by 1 byte
                    break;
                    
                case OP_RAW_VALUE:
                    // Make sure we don't try to read past the end of the data
                    if (i + 5 > data.length) {
                        throw new ScratchException(new IllegalStateException("Incomplete raw value in compressed data"));
                    }
                    // We already checked bounds above, safe to read 4 more bytes
                    int rawValue = (data[i + 1] & 0xFF) |
                                  ((data[i + 2] & 0xFF) << 8) |
                                  ((data[i + 3] & 0xFF) << 16) |
                                  ((data[i + 4] & 0xFF) << 24);
                    
                    // Convert int bits back to float delta and apply delta decoding
                    previousValue += Float.intBitsToFloat(rawValue);
                    floats[floatsIndex++] = previousValue;
                    
                    cache[cacheIndex] = rawValue;
                    cacheIndex = (cacheIndex + 1) % CACHE_SIZE;
                    i += 5; // Advance by 5 bytes (1 opcode + 4 float bytes)
                    break;
            }
        }
        
        if (floatsIndex != expectedLength) {
            throw new ScratchException(new IllegalStateException(
                "Length mismatch: expected " + expectedLength + ", got " + floatsIndex));
        }
        
        return floats;
    }
}