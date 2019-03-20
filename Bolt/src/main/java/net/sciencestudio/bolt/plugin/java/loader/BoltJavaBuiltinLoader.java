package net.sciencestudio.bolt.plugin.java.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.sciencestudio.bolt.plugin.core.container.BoltContainer;
import net.sciencestudio.bolt.plugin.core.loader.BoltLoader;
import net.sciencestudio.bolt.plugin.java.BoltJavaPlugin;
import net.sciencestudio.bolt.plugin.java.container.BoltClassContainer;

public class BoltJavaBuiltinLoader<T extends BoltJavaPlugin> implements BoltLoader<T> {

	private List<Class<? extends T>> custom = new ArrayList<>();
	
	private Class<T> targetClass;
	
	public BoltJavaBuiltinLoader(Class<T> targetClass) {
		this.targetClass = targetClass;
	}
	
	public void load(Class<? extends T> implClass) {
		custom.add(implClass);
	}
	
	@Override
	public List<BoltContainer<T>> getContainers() {
		return custom
				.stream()
				.map(c -> new BoltClassContainer<>(targetClass, c))
				.collect(Collectors.toList());
	}
	
}
