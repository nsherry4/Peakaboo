package net.sciencestudio.bolt.scripting.plugin.loader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.sciencestudio.bolt.plugin.core.container.BoltContainer;
import net.sciencestudio.bolt.plugin.core.loader.BoltLoader;
import net.sciencestudio.bolt.scripting.plugin.BoltScriptPlugin;
import net.sciencestudio.bolt.scripting.plugin.container.BoltScriptContainer;

public class BoltScriptBuiltinLoader<T extends BoltScriptPlugin> implements BoltLoader<T> {

	private List<URL> custom = new ArrayList<>();
	
	private Class<T> targetClass;
	
	public BoltScriptBuiltinLoader(Class<T> targetClass) {
		this.targetClass = targetClass;
	}
	
	public void load(URL resource) {
		custom.add(resource);
	}
	
	@Override
	public List<BoltContainer<T>> getContainers() {
		return custom
				.stream()
				.map(url -> new BoltScriptContainer<>(url, targetClass))
				.collect(Collectors.toList());
	}

}
