package org.peakaboo.framework.bolt.plugin.config.loader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.peakaboo.framework.bolt.plugin.config.BoltConfigPlugin;
import org.peakaboo.framework.bolt.plugin.config.container.BoltConfigContainer;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.loader.BoltLoader;

public class BoltConfigBuiltinLoader<T extends BoltConfigPlugin> implements BoltLoader<T> {

	private List<URL> custom = new ArrayList<>();
	
	private BoltPluginManager<T> manager;
	private Class<T> targetClass;
	private Function<String, T> builder;
	
	
	public BoltConfigBuiltinLoader(BoltPluginManager<T> manager, Class<T> targetClass, Function<String, T> builder) {
		this.manager = manager;
		this.targetClass = targetClass;
		this.builder = builder;
	}
	
	public void load(URL resource) {
		custom.add(resource);
	}
	
	@Override
	public List<BoltContainer<T>> getContainers() {
		return custom
				.stream()
				.map(url -> new BoltConfigContainer<>(manager, url, targetClass, builder))
				.collect(Collectors.toList());
	}

}
