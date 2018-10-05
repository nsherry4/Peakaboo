package net.sciencestudio.bolt.plugin.core;

import java.net.URL;

import net.sciencestudio.bolt.plugin.java.BoltJavaPlugin;
import net.sciencestudio.bolt.plugin.java.ClassInstantiationException;

public interface BoltClassloaderPluginLoader<T extends BoltJavaPlugin> extends BoltPluginLoader<T> {

	BoltPluginPrototype<T> registerPlugin(Class<? extends T> loadedClass) throws ClassInstantiationException;

	BoltPluginPrototype<T> registerPlugin(Class<? extends T> loadedClass, URL source)
			throws ClassInstantiationException;

	void registerBuiltIn();

}