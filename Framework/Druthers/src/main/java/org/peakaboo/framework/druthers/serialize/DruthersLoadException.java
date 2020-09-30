package org.peakaboo.framework.druthers.serialize;

import org.yaml.snakeyaml.error.YAMLException;

public class DruthersLoadException extends RuntimeException {
	public DruthersLoadException(YAMLException e) {
		super(e);
	}
}
