package net.sciencestudio.scratch.encoders.serializers;

import org.nustaq.serialization.FSTConfiguration;

import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.ScratchException;

public class FSTSerializingEncoder<T> implements ScratchEncoder<T>{

	private FSTConfiguration conf = FSTConfiguration.createUnsafeBinaryConfiguration();
	
	public FSTSerializingEncoder(Class<? extends T> clazz, Class<?>... classes) {
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
		return "FST Serializer";
	}
	

}
