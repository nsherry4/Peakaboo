package org.peakaboo.framework.scratch.encoders.compressors;

import java.nio.ByteBuffer;

import org.peakaboo.framework.scratch.ScratchEncoder;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;


public class LZ4FastCompressionEncoder implements ScratchEncoder<byte[]>{

	private LZ4Compressor compressor = LZ4Factory.fastestInstance().fastCompressor(); 
	private LZ4FastDecompressor decompressor = LZ4Factory.fastestInstance().fastDecompressor();
	
	public LZ4FastCompressionEncoder() {}

	
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
		return "LZ4 Fast Compressor";
	}
}
