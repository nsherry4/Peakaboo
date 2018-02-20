package net.sciencestudio.scratch.encoders.serializers;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.io.UnsafeMemoryInput;
import com.esotericsoftware.kryo.io.UnsafeMemoryOutput;

import net.sciencestudio.scratch.ScratchEncoder;
import net.sciencestudio.scratch.ScratchException;

public class KryoSerializingEncoder<T> implements ScratchEncoder<T>{

	private final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();
			for (Class<?> key : registrations.keySet()) {
				Optional<Serializer<?>> serializer = registrations.get(key);
				if (serializer.isPresent()) {
					kryo.register(key, serializer.get());
				} else {
					kryo.register(key);
				}
			}
			return kryo;
		};
	};
	
	private Map<Class<?>, Optional<Serializer<?>>> registrations = new HashMap<>();
	
	
	private Class<? extends T> clazz;
	private boolean supportsUnsafe = false;
	
	public KryoSerializingEncoder(Class<? extends T> clazz, Class<?>... others) {
		this.clazz = clazz;
		register(clazz);
		for (Class<?> other : others) {
			register(other);
		}
		try {
			Output test = new UnsafeMemoryOutput();
			Kryo kryo = new Kryo();
			kryo.writeObject(test, "Hello");
			supportsUnsafe = true;
		} catch (Exception e) {
			supportsUnsafe = false;
		}
		supportsUnsafe = false;
	}
	
	public void register(Class<?> c)
	{
		registrations.put(c, Optional.ofNullable(null));
	}

	public void register(Class<?> c, Serializer<T> s)
	{
		registrations.put(c, Optional.ofNullable(s));
	}

	
	private Kryo getKryo() {
		return kryos.get();
	}
	
	@Override
	public byte[] encode(T data) throws ScratchException {
		Output kOut;
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		if (supportsUnsafe) {
			kOut = new UnsafeMemoryOutput(bOut);
		} else {
			kOut = new Output(bOut);
		}
		getKryo().writeObject(kOut, data);
		kOut.close();
		return bOut.toByteArray();
	}

	@Override
	public T decode(byte[] data) throws ScratchException {
		Input kIn;
		if (supportsUnsafe) {
			kIn = new UnsafeMemoryInput(data);
		} else {
			kIn = new Input(data);
		}
		return getKryo().readObject(kIn, clazz);
	}

	public String toString() {
		return "Kryo Serializer";
	}
	
	
}
