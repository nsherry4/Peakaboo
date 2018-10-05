package net.sciencestudio.bolt.plugin.core;

import java.io.File;
import java.net.URL;

import net.sciencestudio.bolt.plugin.java.BoltJavaPlugin;
import net.sciencestudio.bolt.plugin.java.ClassInstantiationException;

public interface BoltClassloaderPluginLoader<T extends BoltJavaPlugin> {

	BoltPluginPrototype<T> registerPlugin(Class<? extends T> loadedClass) throws ClassInstantiationException;

	BoltPluginPrototype<T> registerPlugin(Class<? extends T> loadedClass, URL source)
			throws ClassInstantiationException;

	void register(File file);

	void register(URL url) throws ClassInstantiationException;

	void register();

}