package net.sciencestudio.bolt.plugin.core;

import net.sciencestudio.bolt.plugin.java.BoltJavaPlugin;

public interface BoltClassloaderPluginLoader<T extends BoltJavaPlugin> extends BoltPluginLoader<T> {

	void registerPlugin(Class<? extends T> loadedClass);

	void registerBuiltIn();

}