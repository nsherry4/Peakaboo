package net.sciencestudio.scratch.encoders.compressors;

import java.nio.ByteBuffer;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.sciencestudio.scratch.ScratchEncoder;


public class LZ4GoodCompressionEncoder implements ScratchEncoder<byte[]>{

	private LZ4Compressor compressor = LZ4Factory.fastestInstance().highCompressor(5); 
	private LZ4FastDecompressor decompressor = LZ4Factory.fastestInstance().fastDecompressor();
	
	public LZ4GoodCompressionEncoder() {}

	
	public byte[] encode(byte[] input) {

		byte[] compressed = compressor.compress(input);
		
		ByteBuffer full = ByteBuffer.wrap(new byte[compressed.length+4]);
		full.putInt(input.length);
		full.put(compressed);
		
		return full.array();
		
	}

	public byte[] decode(byte[] input) {
		ByteBuffer data = ByteBuffer.wrap(input);
		int length = data.getInt();
		return decompressor.decompress(input, 4, length);
	}

	public String toString() {
		return "LZ4 Good Compressor";
	}
}
