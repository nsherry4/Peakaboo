package org.peakaboo.framework.scratch.encoders.compressors;

import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;
import org.xerial.snappy.Snappy;

import java.io.IOException;
import java.util.Arrays;

public class NoCompressionEncoder implements ScratchEncoder<byte[]> {

    @Override
    public byte[] encode(byte[] data) throws ScratchException {
        return Arrays.copyOf(data, data.length);
    }

    @Override
    public byte[] decode(byte[] data) throws ScratchException {
        return Arrays.copyOf(data, data.length);
    }

    public String toString() {
        return "No Compressor";
    }

}
