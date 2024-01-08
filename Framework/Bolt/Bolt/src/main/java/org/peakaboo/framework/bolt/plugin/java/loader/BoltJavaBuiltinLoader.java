package org.peakaboo.framework.bolt.plugin.java.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.loader.BoltLoader;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.bolt.plugin.java.container.BoltClassContainer;

public class BoltJavaBuiltinLoader<T extends BoltJavaPlugin> implements BoltLoader<T> {

	private List<Class<? extends T>> custom = new ArrayList<>();
	
	private Class<T> targetClass;
	private PluginRegistry<T> manager;
	
	public BoltJavaBuiltinLoader(PluginRegistry<T> manager, Class<T> targetClass) {
		this.targetClass = targetClass;
	}
	
	public void load(Class<? extends T> implClass) {
		custom.add(implClass);
	}
	
	@Override
	public List<BoltContainer<T>> getContainers() {
		return custom
				.stream()
				.map(c -> new BoltClassContainer<>(this.manager, this.targetClass, c))
				.collect(Collectors.toList());
	}
	
}
