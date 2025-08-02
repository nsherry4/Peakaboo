package org.peakaboo.dataset.encoder;

import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;

import java.nio.ByteBuffer;

/**
 * Inspired by the QOI image format (https://qoiformat.org/), this is the QOXRF
 * or Quite Okay XRF format. It uses delta encoding plus simple compression
 * methods like run length encoding and a 64 entry recent value cache. Two-bit
 * opcodes leave 6 bits for length of run or recent value offset. This is
 * enough that we can pack most channel values into a single byte. The raw,
 * uncompressable float values are packed into four dedicated bytes.<br><br>
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
 * reference. In those cases, rounding off values near zero *before* delta
 * encoding may improve compression on float data sets without a significant
 * loss in precision.
 *
 */

public class QOXRF implements ScratchEncoder<Spectrum> {
    
    private static final int OP_RLE_ZERO = 0;    // 00
    private static final int OP_CACHE_REF = 1;   // 01
    private static final int OP_RAW_VALUE = 2;   // 10
    private static final int OP_RESERVED = 3;    // 11
    
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
        int totalEncoded = 0;
        while (i < values.length) {
            int current = values[i];

            // Check for zero run-length encoding
            if (current == 0) {
                int runLength = 1;
                while (i + runLength < values.length && 
                       values[i + runLength] == 0 && 
                       runLength < MAX_RLE_LENGTH) {
                    runLength++;
                }
                // Encode: 00xxxxxx (RLE_ZERO with 6-bit length) - 1 byte total
                compressed.put((byte) ((OP_RLE_ZERO << 6) | (runLength - 1)));
                totalEncoded += runLength;
                i += runLength;
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
                
                totalEncoded++;
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
            
            totalEncoded++;
            i++;
        }

        if (totalEncoded != values.length) {
            throw new IllegalStateException("Encoder bug: expected to encode " + values.length + " values, but encoded " + totalEncoded);
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
            // Ensure we can read at least the opcode byte
            if (i >= data.length) {
                throw new ScratchException(new IllegalStateException("Unexpected end of data"));
            }
            
            // Read first byte to determine opcode
            int firstByte = data[i] & 0xFF;
            int opcode = (firstByte >>> 6) & 0x3;
            
            // Check bounds based on opcode BEFORE trying to read additional bytes
            switch (opcode) {
                case OP_RLE_ZERO:
                case OP_CACHE_REF:
                    // These are 1 byte total - we already have what we need
                    break;
                case OP_RAW_VALUE:
                    if (i + 5 > data.length) {
                        throw new ScratchException(new IllegalStateException("Incomplete raw value in compressed data"));
                    }
                    break;
                case OP_RESERVED:
                    throw new ScratchException(new IllegalArgumentException("Reserved opcode encountered"));
            }
            
            // Now process the opcode safely
            switch (opcode) {
                case OP_RLE_ZERO:
                    int runLength = (firstByte & 0x3F) + 1;
                    for (int j = 0; j < runLength && floatsIndex < expectedLength; j++) {
                        // Zero delta means same value as previous
                        floats[floatsIndex] = previousValue;
                        floatsIndex++;
                    }
                    i++; // Advance by 1 byte
                    break;
                    
                case OP_CACHE_REF:
                    int cacheOffset = firstByte & 0x3F;
                    int cachePos = (cacheIndex - 1 - cacheOffset + CACHE_SIZE) % CACHE_SIZE;
                    int cachedValue = cache[cachePos];
                    
                    // Convert int bits back to float delta and apply delta decoding
                    float delta = Float.intBitsToFloat(cachedValue);
                    previousValue = (floatsIndex == 0) ? delta : previousValue + delta;
                    floats[floatsIndex++] = previousValue;
                    
                    cache[cacheIndex] = cachedValue;
                    cacheIndex = (cacheIndex + 1) % CACHE_SIZE;
                    i++; // Advance by 1 byte
                    break;
                    
                case OP_RAW_VALUE:
                    // We already checked bounds above, safe to read 4 more bytes
                    int rawValue = (data[i + 1] & 0xFF) |
                                  ((data[i + 2] & 0xFF) << 8) |
                                  ((data[i + 3] & 0xFF) << 16) |
                                  ((data[i + 4] & 0xFF) << 24);
                    
                    // Convert int bits back to float delta and apply delta decoding
                    float rawDelta = Float.intBitsToFloat(rawValue);
                    previousValue = (floatsIndex == 0) ? rawDelta : previousValue + rawDelta;
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