package org.peakaboo.framework.scratch.encoders.compressors;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdException;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;

public class ZStdCompressionEncoder implements ScratchEncoder<byte[]> {

    @Override
    public byte[] encode(byte[] data) throws ScratchException {
        try {
            return Zstd.compress(data);
        } catch (ZstdException e) {
            throw new ScratchException(e);
        }
    }

    @Override
    public byte[] decode(byte[] data) throws ScratchException {
        try {
            return Zstd.decompress(data);
        } catch (ZstdException e) {
            throw new ScratchException(e);
        }
    }

    public String toString() {
        return "ZStd Compressor";
    }
}
