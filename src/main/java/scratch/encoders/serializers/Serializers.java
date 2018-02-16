package scratch.encoders.serializers;

import java.io.Serializable;

import scratch.ScratchEncoder;
import scratch.encoders.serializers.SerializingEncoder;

public class Serializers {

	public static <T extends Serializable> ScratchEncoder<T> java() {
		return new SerializingEncoder<>();
	}

	public static <T> ScratchEncoder<T> kryo(Class<? extends T> clazz, Class<?>... others) {
		return new KryoSerializingEncoder<>(clazz, others);
	}
}
