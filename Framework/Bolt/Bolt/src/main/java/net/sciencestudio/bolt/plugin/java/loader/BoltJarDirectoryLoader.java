package net.sciencestudio.bolt.plugin.java.loader;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import net.sciencestudio.bolt.plugin.core.container.BoltContainer;
import net.sciencestudio.bolt.plugin.core.loader.BoltDirectoryLoader;
import net.sciencestudio.bolt.plugin.java.BoltJavaPlugin;
import net.sciencestudio.bolt.plugin.java.container.BoltJarContainer;

public class BoltJarDirectoryLoader<T extends BoltJavaPlugin> extends BoltDirectoryLoader<T> {

	
	private Class<T> targetClass;
	
	public BoltJarDirectoryLoader(Class<T> targetClass) {
		super(BoltDirectoryLoader.getLocalDirectory(BoltJarDirectoryLoader.class), false);
		this.targetClass = targetClass;
	}
	
	public BoltJarDirectoryLoader(Class<T> targetClass, File directory) {
		super(directory, true);
		this.targetClass = targetClass;
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
		return new BoltJarContainer<>(targetClass, url);
	}
	
}
