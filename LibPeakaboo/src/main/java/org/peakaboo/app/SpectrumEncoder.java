package org.peakaboo.app;

import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class SpectrumEncoder implements ScratchEncoder<Spectrum> {

    @Override
    public byte[] encode(Spectrum data) throws ScratchException {
        float[] floats = data.backingArray();
        ByteBuffer buffer = ByteBuffer.allocate(floats.length * 4)
                .order(ByteOrder.nativeOrder());
        buffer.asFloatBuffer().put(floats);
        return buffer.array();
    }

    @Override
    public Spectrum decode(byte[] data) throws ScratchException {
        FloatBuffer buffer = ByteBuffer.wrap(data)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        float[] floats = new float[buffer.remaining()];
        buffer.get(floats);
        return new ArraySpectrum(floats, false);
    }
}