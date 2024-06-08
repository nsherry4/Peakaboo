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
	
	public static <T> ScratchEncoder<T> fstUnsafe(Class<? extends T> clazz, Class<?>... classes) {
		if (isAndroid()) {
			// Android doesn't do unsafe, fall back to safe android implementation
			return fst(clazz, classes);
		} else {
			try {
				return new FSTUnsafeSerializingEncoder<>(clazz, classes);
			} catch (InaccessibleObjectException e) {
				ScratchLog.get().log(Level.WARNING, "Could not create unsafe FST serializer, falling back to safe implementation.", e);
				return new FSTDefaultSerializingEncoder<>(clazz, classes);
			}
		}
	}
	
	public static <T> ScratchEncoder<T> fst(Class<? extends T> clazz, Class<?>... classes) {
		if (isAndroid()) {
			return new FSTAndroidSerializingEncoder<>(clazz, classes);
		} else {
			return new FSTDefaultSerializingEncoder<>(clazz, classes);
		}
	}
	
	private static boolean isAndroid() {
		return System.getProperty("java.vendor").equals("The Android Project");
	}
	
}

