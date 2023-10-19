package org.peakaboo.framework.bolt.plugin.config.loader;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.peakaboo.framework.bolt.plugin.config.BoltConfigPlugin;
import org.peakaboo.framework.bolt.plugin.config.BoltConfigPluginBuilder;
import org.peakaboo.framework.bolt.plugin.config.container.BoltConfigContainer;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.loader.BoltDirectoryLoader;

public class BoltConfigDirectoryLoader<T extends BoltConfigPlugin> extends BoltDirectoryLoader<T>{

	private String ext;
	private BoltConfigPluginBuilder<T> builder;
	private Class<T> targetClass;
	private BoltPluginManager<T> manager;

	public BoltConfigDirectoryLoader(BoltPluginManager<T> manager, Class<T> targetClass, File directory, String ext, BoltConfigPluginBuilder<T> builder) {
		super(directory, true);
		this.ext = ext;
		this.targetClass = targetClass;
		this.builder = builder;
		this.manager = manager;
	}

	@Override
	public List<BoltContainer<T>> getContainers() {
		List<File> files = super.scanFiles(p -> p.toString().toLowerCase().endsWith(ext));
		return files.stream()
			.map(this::build)
			.filter(Objects::nonNull)
			.filter(this::unmanagedNotEmpty)
			.collect(Collectors.toList());

	}
	
	@Override
	public BoltConfigContainer<T> build(File file) {
		URL url = fileToURL(file);
		if (url == null) { return null; }
		return new BoltConfigContainer<>(this.manager, url, targetClass, builder, true);
	}
	
}
