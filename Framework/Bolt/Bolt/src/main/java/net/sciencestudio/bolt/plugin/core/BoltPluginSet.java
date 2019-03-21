package net.sciencestudio.bolt.plugin.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sciencestudio.bolt.plugin.core.issue.BoltIssue;
import net.sciencestudio.bolt.plugin.core.issue.BoltOldPluginIssue;

/**
 * Convenience class to manage plugins and issues. Not generally intended for
 * high-level use.
 */
public class BoltPluginSet<T extends BoltPlugin> implements BoltPluginCollection<T> {

	private ArrayList<BoltPluginPrototype<? extends T>> plugins = new ArrayList<>();
	private ArrayList<BoltIssue<? extends T>> issues = new ArrayList<>();
	
	
	public List<BoltPluginPrototype<? extends T>> getPlugins() {
		return Collections.unmodifiableList(plugins);
	}

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
				if (plugin.isNewerThan(existingPlugin)) {
					addIssue(new BoltOldPluginIssue<>(existingPlugin));
				}
				plugins.add(plugin);
			}
			
		} else {
			plugins.add(plugin);	
		}
		
	}

	public void loadFrom(BoltPluginCollection<? extends T> pluginset) {
		for (BoltPluginPrototype<? extends T> t : pluginset.getPlugins()) {
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
	

}
