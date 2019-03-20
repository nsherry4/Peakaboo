package net.sciencestudio.bolt.plugin.config;

import java.io.File;
import java.net.URL;
import java.util.function.Function;

import net.sciencestudio.bolt.plugin.core.BoltFilesytstemPluginLoader;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;
import net.sciencestudio.bolt.plugin.core.container.BoltContainer;

public class IBoltConfigPluginLoader<T extends BoltConfigPlugin> implements BoltFilesytstemPluginLoader<T>{

	private BoltPluginSet<? super T> plugins;
	private Function<String, T> builder;
	private String ext;
	private Class<T> pluginClass;
	
	public IBoltConfigPluginLoader(BoltPluginSet<? super T> plugins, Class<T> pluginClass, String ext, Function<String, T> builder) {
		this.plugins = plugins;
		this.builder = builder;
		this.ext = ext;
		this.pluginClass = pluginClass;
	}
	
	@Override
	public void scanDirectory(File directory) {	
		for (File file : directory.listFiles()) {
			if (! file.getName().endsWith(ext)) continue;
			register(file);
		}
	}
		
	@Override
	public void registerURL(URL url) {
		BoltContainer<T> container = new BoltConfigContainer<>(url, pluginClass, builder);
		plugins.loadFrom(container.getPlugins());
	}
	
	

}
