package org.peakaboo.framework.scratch.encoders.serializers;

import org.nustaq.serialization.FSTConfiguration;
import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchException;

public class FSTUnsafeSerializingEncoder<T> implements ScratchEncoder<T>{

	private FSTConfiguration conf;
	
	public FSTUnsafeSerializingEncoder(Class<? extends T> clazz, Class<?>... classes) {
		this.conf = FSTConfiguration.createUnsafeBinaryConfiguration();	
		conf.registerClass(clazz);
		conf.registerClass(classes);
	}
	
	@Override
	public byte[] encode(T data) throws ScratchException {
		return conf.asByteArray(data);
	}

	@Override
	public T decode(byte[] data) throws ScratchException {
		return (T) conf.asObject(data);
	}
	
	public String toString() {
		return "FST Serializer (Unsafe)";
	}
	

}
