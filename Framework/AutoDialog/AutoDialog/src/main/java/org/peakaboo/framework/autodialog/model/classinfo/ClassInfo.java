package org.peakaboo.framework.autodialog.model.classinfo;

public interface ClassInfo<T> {

	String serialize(T value);
	T deserialize(String data);
	Class<T> getValueClass();
	
}
