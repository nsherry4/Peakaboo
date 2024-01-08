package org.peakaboo.framework.bolt.plugin.java.loader;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.loader.BoltDirectoryLoader;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.bolt.plugin.java.container.BoltJarContainer;

public class BoltJarDirectoryLoader<T extends BoltJavaPlugin> extends BoltDirectoryLoader<T> {

	
	private Class<T> targetClass;
	private PluginRegistry<T> manager;
	
	public BoltJarDirectoryLoader(PluginRegistry<T> manager, Class<T> targetClass) {
		super(BoltDirectoryLoader.getLocalDirectory(BoltJarDirectoryLoader.class), false);
		this.targetClass = targetClass;
		this.manager = manager;
	}
	
	public BoltJarDirectoryLoader(PluginRegistry<T> manager, Class<T> targetClass, File directory) {
		super(directory, true);
		this.targetClass = targetClass;
		this.manager = manager;
	}

	private List<File> getJars() {
		return scanFiles(file -> file.getPath().toLowerCase().endsWith(".jar"));
	}
	
	@Override
	public List<BoltContainer<T>> getContainers() {
		return getJars().stream()
				.map(f -> build(f))
				.filter(c -> c != null)
				.filter(c -> unmanagedNotEmpty(c))
				.collect(Collectors.toList());
	}
	

	@Override
	public BoltJarContainer<T> build(File file) {
		URL url = fileToURL(file);
		if (url == null) { return null; }
		return new BoltJarContainer<>(this.manager, this.targetClass, url);
	}
	
}
