package net.sciencestudio.scratch.encoders.serializers;

import java.io.Serializable;

import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.encoders.serializers.SerializingEncoder;

public class Serializers {

	public static <T extends Serializable> ScratchEncoder<T> java() {
		return new SerializingEncoder<>();
	}

	public static <T> ScratchEncoder<T> kryo(Class<? extends T> clazz, Class<?>... others) {
		return new KryoSerializingEncoder<>(clazz, others);
	}
	
	public static <T> ScratchEncoder<T> fst(Class<? extends T> clazz, Class<?>... classes) {
		return new FSTSerializingEncoder<>(clazz, classes);
	}
}
