package org.peakaboo.framework.scratch.encoders.compressors;

import org.peakaboo.framework.scratch.ScratchEncoder;

public class Compressors {

	public static ScratchEncoder<byte[]> deflate() {
		return new DeflateCompressionEncoder();
	}
	
	public static ScratchEncoder<byte[]> lz4fast() {
		return new LZ4FastCompressionEncoder();
	}
	
	public static ScratchEncoder<byte[]> lz4good() {
		return new LZ4GoodCompressionEncoder();
	}
	
	public static ScratchEncoder<byte[]> snappy() {
		return new SnappyCompressionEncoder();
	}

	public static ScratchEncoder<byte[]> zstd() { return new ZStdCompressionEncoder(); }

	public static ScratchEncoder<byte[]> none() { return new NoCompressionEncoder(); }
	
}
