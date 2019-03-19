package net.sciencestudio.bolt.plugin.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.sciencestudio.bolt.plugin.core.issue.BoltIssue;
import net.sciencestudio.bolt.plugin.core.issue.BoltOldPluginIssue;

public class IBoltPluginSet<T extends BoltPlugin> implements BoltPluginSet<T> {

	private ArrayList<BoltPluginPrototype<? extends T>> plugins = new ArrayList<>();
	private ArrayList<BoltIssue> issues = new ArrayList<>();
	
	@Override
	public List<BoltPluginPrototype<? extends T>> getAll() {
		return new ArrayList<>(plugins);
	}

	@Override
	public List<T> newInstances() {
		List<T> insts = plugins.stream().map(p -> p.create()).collect(Collectors.toList());
		Collections.sort(insts, (f1, f2) -> f1.pluginName().compareTo(f1.pluginName()));
		return insts;
	}

	@Override
	public void addPlugin(BoltPluginPrototype<? extends T> plugin) {
		if (plugins.contains(plugin)) {
			return;
		}
		String uuid = plugin.getUUID();
		if (this.hasUUID(uuid)) {
			//there is already a plugin with the same UUID.
			//we have to choose which of these to load
			BoltPluginPrototype<? extends T> existingPlugin = this.getByUUID(uuid);
			
			if (plugin.isUpgradeFor(existingPlugin)) {
				plugins.remove(existingPlugin);
				addIssue(new BoltOldPluginIssue(existingPlugin));
				plugins.add(plugin);
			}
			
		} else {
			plugins.add(plugin);	
		}
		
	}

	@Override
	public BoltPluginPrototype<? extends T> getByUUID(String uuid) {
		for (BoltPluginPrototype<? extends T> plugin : plugins) {
			if (plugin.getUUID().equals(uuid)) {
				return plugin;
			}
		}
		return null;
	}

	@Override
	public List<BoltIssue> getIssues() {
		return Collections.unmodifiableList(issues);
	}
	
	public void addIssue(BoltIssue issue) {
		issues.add(issue);
	}
	

}
