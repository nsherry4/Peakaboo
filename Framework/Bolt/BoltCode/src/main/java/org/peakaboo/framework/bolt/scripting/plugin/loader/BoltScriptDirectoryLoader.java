package org.peakaboo.framework.bolt.scripting.plugin.loader;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.peakaboo.framework.bolt.scripting.plugin.BoltScriptPlugin;
import org.peakaboo.framework.bolt.scripting.plugin.container.BoltScriptContainer;

import org.peakaboo.framework.bolt.plugin.config.BoltConfigPlugin;
import org.peakaboo.framework.bolt.plugin.config.container.BoltConfigContainer;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.loader.BoltDirectoryLoader;

public class BoltScriptDirectoryLoader<T extends BoltScriptPlugin> extends BoltDirectoryLoader<T>{

	private String ext;
	private Class<T> targetClass;
	
	public BoltScriptDirectoryLoader(Class<T> targetClass, File directory, String ext) {
		super(directory, true);
		this.ext = ext;
		this.targetClass = targetClass;
	}

	@Override
	public List<BoltContainer<T>> getContainers() {
		List<File> files = super.scanFiles(p -> p.toString().toLowerCase().endsWith(ext));
		return files.stream()
			.map(f -> build(f))
			.filter(c -> c != null)
			.filter(c -> unmanagedNotEmpty(c))
			.collect(Collectors.toList());

	}
	
	@Override
	public BoltScriptContainer<T> build(File file) {
		URL url = fileToURL(file);
		if (url == null) { return null; }
		return new BoltScriptContainer<>(url, targetClass);
	}
	
}
