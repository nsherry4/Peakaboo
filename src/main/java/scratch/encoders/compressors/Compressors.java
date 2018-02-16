package scratch.encoders.compressors;

import scratch.ScratchEncoder;

public class Compressors {

	public static ScratchEncoder<byte[]> deflate() {
		return new DeflateCompressionEncoder();
	}
	
	public static ScratchEncoder<byte[]> lz4() {
		return new LZ4CompressionEncoder();
	}
	
}
