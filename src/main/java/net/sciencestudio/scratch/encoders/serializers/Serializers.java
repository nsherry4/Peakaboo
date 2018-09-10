package net.sciencestudio.scratch.encoders.serializers;

import java.io.Serializable;

import net.sciencestudio.scratch.ScratchEncoder;

public class Serializers {

	public static <T extends Serializable> ScratchEncoder<T> java() {
		return new SerializingEncoder<>();
	}

	public static <T> ScratchEncoder<T> kryo(Class<? extends T> clazz, Class<?>... others) {
		return new KryoSerializingEncoder<>(clazz, others);
	}
	
	public static <T> ScratchEncoder<T> fstUnsafe(Class<? extends T> clazz, Class<?>... classes) {
		if (isAndroid()) {
			return fst(clazz, classes);
		} else {
			return new FSTUnsafeSerializingEncoder<>(clazz, classes);
		}
	}
	
	public static <T> ScratchEncoder<T> fst(Class<? extends T> clazz, Class<?>... classes) {
		return new FSTDefaultSerializingEncoder<>(clazz, classes);
	}
	
	private static boolean isAndroid() {
		return System.getProperty("java.vendor").equals("The Android Project");
	}
	
}

