package org.peakaboo.framework.druthers;

import org.peakaboo.framework.druthers.serialize.DruthersSerializer;

public interface DruthersStorable {

	default <T extends DruthersStorable> String serialize() {
		return DruthersSerializer.serialize(this);
	}
		
}
