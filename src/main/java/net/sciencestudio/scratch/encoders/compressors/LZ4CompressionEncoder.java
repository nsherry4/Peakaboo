package net.sciencestudio.scratch.encoders.compressors;

import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4SafeDecompressor;
import net.sciencestudio.scratch.ScratchEncoder;


public class LZ4CompressionEncoder implements ScratchEncoder<byte[]>{

	private int maxSize = 2<<16; //16k default max entry size
	private LZ4Compressor compressor = LZ4Factory.fastestInstance().fastCompressor(); 
	private LZ4SafeDecompressor decompressor = LZ4Factory.fastestInstance().safeDecompressor();
	
	public LZ4CompressionEncoder() {}
	public LZ4CompressionEncoder(int maxSize) {
		this.maxSize = maxSize;
	}

	
	public byte[] encode(byte[] input) {
		if (input.length > maxSize) {
			synchronized (this) {
				maxSize = Math.max(input.length, maxSize);	
			}
		}
		
		return compressor.compress(input);
	}

	public byte[] decode(byte[] input) {
		return decompressor.decompress(input, maxSize);
	}

	
}
