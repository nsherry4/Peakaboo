package org.peakaboo.framework.scratch.encoders.serializers;

import java.io.Serializable;
import java.lang.reflect.InaccessibleObjectException;
import java.util.logging.Level;

import org.peakaboo.framework.scratch.ScratchEncoder;
import org.peakaboo.framework.scratch.ScratchLog;

public class Serializers {

	public static <T extends Serializable> ScratchEncoder<T> java() {
		return new SerializingEncoder<>();
	}

	public static <T> ScratchEncoder<T> kryo(Class<? extends T> clazz, Class<?>... others) {
		return new KryoSerializingEncoder<>(clazz, others);
	}

}

