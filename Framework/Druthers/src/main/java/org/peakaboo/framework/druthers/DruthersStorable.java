package org.peakaboo.framework.druthers;

import org.peakaboo.framework.druthers.serialize.YamlSerializer;

public abstract class DruthersStorable {

	public static <T extends DruthersStorable> T deserialize(String yaml, Class<T> cls, boolean strict) {
		return YamlSerializer.deserialize(yaml, cls, strict);
	}
	
	public <T extends DruthersStorable> String serialize() {
		return YamlSerializer.serialize(this);
	}
	
	
}
