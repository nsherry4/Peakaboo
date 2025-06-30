package org.peakaboo.framework.bolt.plugin.java.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.peakaboo.framework.bolt.plugin.core.PluginRegistry;
import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.loader.BoltLoader;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.bolt.plugin.java.container.BoltClassContainer;

public class BoltJavaBuiltinLoader<T extends BoltJavaPlugin> implements BoltLoader<T> {

	private class BuiltinEntry {
		Class<? extends T> cls; 
		Optional<Integer> weight;
		public BuiltinEntry(Class<? extends T> cls, Optional<Integer> weight) {
			this.cls = cls;
			this.weight = weight;
		}
	};
	
	private List<BuiltinEntry> custom = new ArrayList<>();
	
	private Class<T> targetClass;
	private PluginRegistry<T> manager;
	
	public BoltJavaBuiltinLoader(PluginRegistry<T> manager, Class<T> targetClass) {
		this.targetClass = targetClass;
		this.manager = manager;
	}
	
	public void load(Class<? extends T> implClass) {
		custom.add(new BuiltinEntry(implClass, Optional.empty()));
	}
	
	public void load(Class<? extends T> implClass, int weight) {
		custom.add(new BuiltinEntry(implClass, Optional.of(weight)));
	}
	
	@Override
	public List<BoltContainer<T>> getContainers() {
		return custom
				.stream()
				.map(c -> {
					if (c.weight.isPresent()) {
						return new BoltClassContainer<>(this.manager, this.targetClass, c.cls, c.weight.get());
					} else {
						return new BoltClassContainer<>(this.manager, this.targetClass, c.cls);
					}
				})
				.collect(Collectors.toList());
	}
	
}
