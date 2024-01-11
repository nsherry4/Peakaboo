package org.peakaboo.framework.druthers.serialize;

import java.io.IOException;

import org.yaml.snakeyaml.error.YAMLException;

public class DruthersLoadException extends IOException {
	public DruthersLoadException(YAMLException e) {
		super(e);
	}
	public DruthersLoadException(String msg) {
		super(msg);
	}
}
