package org.peakaboo.framework.bolt.plugin.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;
import org.peakaboo.framework.bolt.plugin.core.issue.BoltOldPluginIssue;

/**
 * Convenience class to manage plugins and issues. Not generally intended for
 * high-level use.
 */
public class BoltPluginSet<T extends BoltPlugin> implements PluginCollection<T> {

	//Anything modifying this plugins list should call sort() afterwards
	private ArrayList<PluginDescriptor<? extends T>> plugins = new ArrayList<>();
	private ArrayList<BoltIssue<? extends T>> issues = new ArrayList<>();
	
	private PluginRegistry<T> manager;
	
	public BoltPluginSet(PluginRegistry<T> manager) {
		this.manager = manager;
	}
	
	@Override
	public List<PluginDescriptor<? extends T>> getPlugins() {
		return Collections.unmodifiableList(plugins);
	}

	public void addPlugin(PluginDescriptor<? extends T> plugin) {
		if (plugins.contains(plugin)) {
			return;
		}
		String uuid = plugin.getUUID();
		if (this.hasUUID(uuid)) {
			//there is already a plugin with the same UUID.
			//we have to choose which of these to load
			PluginDescriptor<? extends T> existingPlugin = this.getByUUID(uuid);
			
			if (plugin.isUpgradeFor(existingPlugin)) {
				plugins.remove(existingPlugin);
				if (plugin.isNewerThan(existingPlugin)) {
					addIssue(new BoltOldPluginIssue<>(existingPlugin));
				}
				plugins.add(plugin);
				sort();
			}
			
		} else {
			plugins.add(plugin);
			sort();
		}
		
	}

	public void loadFrom(PluginCollection<? extends T> pluginset) {
		for (PluginDescriptor<? extends T> t : pluginset.getPlugins()) {
			addPlugin(t);
		}
		for (BoltIssue<? extends T> i : pluginset.getIssues()) {
			addIssue(i);
		}
	}
	
	
	
	public List<BoltIssue<? extends T>> getIssues() {
		return Collections.unmodifiableList(issues);
	}
	

	
	public void addIssue(BoltIssue<? extends T> issue) {
		issues.add(issue);
	}

	@Override
	public PluginRegistry<T> getManager() {
		return manager;
	}
	
	private void sort() {
		//Reversed order on purpose to sort higher numbers first
		plugins.sort((a, b) -> Integer.compare(b.getWeight(), a.getWeight()));
	}
	

}
