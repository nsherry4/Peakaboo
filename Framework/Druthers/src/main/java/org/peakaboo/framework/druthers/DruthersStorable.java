package org.peakaboo.framework.druthers;

import java.util.Map;

import org.peakaboo.framework.druthers.serialize.YamlSerializer;

public abstract class DruthersStorable {

	public static <T extends DruthersStorable> T deserialize(String yaml, Class<T> cls, boolean strict) {
		return YamlSerializer.deserialize(yaml, cls, strict);
	}

	public static <T extends DruthersStorable> T deserialize(String yaml, Class<T> cls, boolean strict, String format) {
		return YamlSerializer.deserialize(yaml, cls, strict, format);
	}

	public static <T extends DruthersStorable> T deserialize(String yaml, boolean strict, Map<String, Class<T>> formats) {
		return YamlSerializer.deserialize(yaml, strict, formats);
	}
	
	public <T extends DruthersStorable> String serialize() {
		return YamlSerializer.serialize(this);
	}
	
	
}
