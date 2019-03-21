package net.sciencestudio.bolt.plugin.config.loader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.sciencestudio.bolt.plugin.config.BoltConfigPlugin;
import net.sciencestudio.bolt.plugin.config.container.BoltConfigContainer;
import net.sciencestudio.bolt.plugin.core.container.BoltContainer;
import net.sciencestudio.bolt.plugin.core.loader.BoltLoader;

public class BoltConfigBuiltinLoader<T extends BoltConfigPlugin> implements BoltLoader<T> {

	private List<URL> custom = new ArrayList<>();
	
	private Class<T> targetClass;
	private Function<String, T> builder;
	
	public BoltConfigBuiltinLoader(Class<T> targetClass, Function<String, T> builder) {
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
				.map(url -> new BoltConfigContainer<>(url, targetClass, builder))
				.collect(Collectors.toList());
	}

}
