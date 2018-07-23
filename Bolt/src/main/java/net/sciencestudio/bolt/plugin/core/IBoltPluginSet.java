package net.sciencestudio.bolt.plugin.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class IBoltPluginSet<T extends BoltPlugin> implements BoltPluginSet<T> {

	private ArrayList<BoltPluginController<? extends T>> plugins = new ArrayList<>();
	
	@Override
	public List<BoltPluginController<? extends T>> getAll() {
		return new ArrayList<>(plugins);
	}

	@Override
	public List<T> newInstances() {
		List<T> insts = plugins.stream().map(p -> p.create()).collect(Collectors.toList());
		Collections.sort(insts, (f1, f2) -> f1.pluginName().compareTo(f1.pluginName()));
		return insts;
	}

	@Override
	public void addPlugin(BoltPluginController<? extends T> plugin) {
		if (plugins.contains(plugin)) {
			return;
		}
		if (this.hasUUID(plugin.getUUID())) {
			return;
		}
		plugins.add(plugin);
	}

	@Override
	public BoltPluginController<? extends T> getByUUID(String uuid) {
		for (BoltPluginController<? extends T> plugin : plugins) {
			if (plugin.getUUID().equals(uuid)) {
				return plugin;
			}
		}
		return null;
	}

}
