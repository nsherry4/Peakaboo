package org.peakaboo.framework.scratch.encoders.serializers;

import java.io.Serializable;

import org.peakaboo.framework.scratch.ScratchEncoder;

public class Serializers {

	public static <T extends Serializable> ScratchEncoder<T> java() {
		return new JavaSerializingEncoder<>();
	}

	public static <T> ScratchEncoder<T> kryo(Class<? extends T> clazz, Class<?>... others) {
		return new KryoSerializingEncoder<>(clazz, others);
	}

}

