package scratch.encoders.compressors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import scratch.ScratchEncoder;
import scratch.ScratchException;

public class DeflateCompressionEncoder implements ScratchEncoder<byte[]>{

	int level = 2;
	public DeflateCompressionEncoder() {
		this(2);
	}
	public DeflateCompressionEncoder(int level) {
		this.level = level;
	}
	
	@Override
	public byte[] decode(byte[] data) {

		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		InflaterInputStream iin = new InflaterInputStream(bais);
		ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
		
		try {
			int b;  
			while ((b = iin.read()) != -1) {
				bout.write(b);
			}
			iin.close();
		} catch (IOException e) {
			throw new ScratchException(e);
		}
			
		return bout.toByteArray();
		
	}

	@Override
	public byte[] encode(byte[] data) {
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DeflaterOutputStream dout = new DeflaterOutputStream(baos, new Deflater(level));
		
		try {
			dout.write(data);
			dout.close();
		} catch (IOException e) {
			throw new ScratchException(e);
		}
		
		return baos.toByteArray();
		
	}
	
}
