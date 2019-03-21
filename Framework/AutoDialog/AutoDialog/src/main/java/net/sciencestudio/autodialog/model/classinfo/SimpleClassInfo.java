package net.sciencestudio.autodialog.model.classinfo;

import java.util.function.Function;

public class SimpleClassInfo<T> implements ClassInfo<T>{

	Class<T> cls;
	Function<T, String> serializer;
	Function<String, T> deserializer;
	
	public SimpleClassInfo(Class<T> cls, Function<T, String> serializer, Function<String, T> deserializer) {
		this.cls = cls;
		this.serializer = serializer;
		this.deserializer = deserializer;
	}
	
	
	@Override
	public String serialize(T value) {
		return this.serializer.apply(value);
	}

	@Override
	public T deserialize(String string) {
		return this.deserializer.apply(string);
	}

	@Override
	public Class<T> getValueClass() {
		return this.cls;
	}

}
