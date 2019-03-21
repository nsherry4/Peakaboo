package org.peakaboo.framework.bolt.scripting.plugin.loader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.peakaboo.framework.bolt.scripting.plugin.BoltScriptPlugin;
import org.peakaboo.framework.bolt.scripting.plugin.container.BoltScriptContainer;

import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;
import org.peakaboo.framework.bolt.plugin.core.loader.BoltLoader;

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
