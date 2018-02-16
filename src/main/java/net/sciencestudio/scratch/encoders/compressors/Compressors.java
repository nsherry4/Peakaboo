package net.sciencestudio.scratch.encoders.compressors;

import net.sciencestudio.scratch.ScratchEncoder;

public class Compressors {

	public static ScratchEncoder<byte[]> deflate() {
		return new DeflateCompressionEncoder();
	}
	
	public static ScratchEncoder<byte[]> lz4() {
		return new LZ4CompressionEncoder();
	}
	
}
