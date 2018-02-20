package net.sciencestudio.scratch.encoders.compressors;

import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.ScratchException;

import java.io.IOException;

import org.xerial.snappy.Snappy;

public class SnappyCompressionEncoder implements ScratchEncoder<byte[]> {

	@Override
	public byte[] encode(byte[] data) throws ScratchException {
		try {
			return Snappy.compress(data);
		} catch (IOException e) {
			throw new ScratchException(e);
		}
	}

	@Override
	public byte[] decode(byte[] data) throws ScratchException {
		try {
			return Snappy.uncompress(data);
		} catch (IOException e) {
			throw new ScratchException(e);
		}
	}
	
	public String toString() {
		return "Snappy Compressor";
	}

}
