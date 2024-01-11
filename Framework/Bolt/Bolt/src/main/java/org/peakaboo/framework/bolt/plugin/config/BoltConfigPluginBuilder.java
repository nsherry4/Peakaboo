package org.peakaboo.framework.bolt.plugin.config;

import java.io.IOException;

@FunctionalInterface
public interface BoltConfigPluginBuilder<T> {
	public T build(String config) throws IOException;
}