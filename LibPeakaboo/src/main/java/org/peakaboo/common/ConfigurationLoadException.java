package org.peakaboo.common;

import org.yaml.snakeyaml.error.YAMLException;

public class ConfigurationLoadException extends RuntimeException {
	public ConfigurationLoadException(YAMLException e) {
		super(e);
	}
}
