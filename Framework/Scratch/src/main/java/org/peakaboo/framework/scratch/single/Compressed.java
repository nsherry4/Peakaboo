package org.peakaboo.framework.scratch.single;

import java.io.Serializable;

import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.encoders.CompoundEncoder;
import org.peakaboo.framework.scratch.encoders.compressors.Compressors;
import org.peakaboo.framework.scratch.encoders.serializers.Serializers;

public class Compressed<T> implements ByteStorage<T> {

	byte[] data;
	ScratchEncoder<T> encoder;
	
	public static <T extends Serializable> Compressed<T> create(T value) {
		return create(value, new CompoundEncoder<T>(Serializers.java(), Compressors.deflate()));
	}
	
	public static <T> Compressed<T> create(T value, ScratchEncoder<T> encoder) {
		Compressed<T> c = new Compressed<>();
		c.data = encoder.encode(value);
		c.encoder = encoder;
		return c;
	}
	
	@Override
	public T get() {
		return this.encoder.decode(this.data);
	}
	
	@Override
	public byte[] getBytes() {
		return data;
	}

	@Override
	public ScratchEncoder<T> getEncoder() {
		return encoder;
	}
	
}
