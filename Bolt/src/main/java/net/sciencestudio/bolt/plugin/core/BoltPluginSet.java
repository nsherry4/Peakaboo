package net.sciencestudio.bolt.plugin.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.sciencestudio.bolt.plugin.core.issue.BoltIssue;
import net.sciencestudio.bolt.plugin.core.issue.BoltOldPluginIssue;

public class BoltPluginSet<T extends BoltPlugin> {

	private ArrayList<BoltPluginPrototype<? extends T>> plugins = new ArrayList<>();
	private ArrayList<BoltIssue> issues = new ArrayList<>();
	
	
	public List<BoltPluginPrototype<? extends T>> getAll() {
		return new ArrayList<>(plugins);
	}

	
	public List<T> newInstances() {
		List<T> insts = plugins.stream().map(p -> p.create()).collect(Collectors.toList());
		Collections.sort(insts, (f1, f2) -> f1.pluginName().compareTo(f1.pluginName()));
		return insts;
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
				addIssue(new BoltOldPluginIssue(existingPlugin));
				plugins.add(plugin);
			}
			
		} else {
			plugins.add(plugin);	
		}
		
	}

	public void loadFrom(BoltPluginSet<? extends T> pluginset) {
		for (BoltPluginPrototype<? extends T> t : pluginset.getAll()) {
			addPlugin(t);
		}
		for (BoltIssue i : pluginset.getIssues()) {
			addIssue(i);
		}
	}
	
	
	public BoltPluginPrototype<? extends T> getByUUID(String uuid) {
		for (BoltPluginPrototype<? extends T> plugin : plugins) {
			if (plugin.getUUID().equals(uuid)) {
				return plugin;
			}
		}
		return null;
	}

	public boolean hasUUID(String uuid) {
		return getByUUID(uuid) != null;
	}
	
	public int size() {
		return getAll().size();
	}

	
	/**
	 * Checks this set against the given one to determine if this 
	 * set is a proper upgrade for the given set. This is only true
	 * if this set contains the other set, and all matching elements 
	 * have version numbers greater than or equal to their 
	 * counterparts in the other set. 
	 * @param other the other set to compare against
	 * @return true if this set is a proper upgrade for the other, false otherwise
	 */
	public boolean isUpgradeFor(BoltPluginSet<T> other) {
		//get all the UUIDs from the other plugin set
		List<String> otherUUIDs = other.getAll().stream().map(p -> p.getUUID()).collect(Collectors.toList());
				
		//if this set is missing any of the UUIDs, it's not an upgrade
		for (String otherUUID : otherUUIDs) {
			if (!this.hasUUID(otherUUID)) {
				return false;
			}
			boolean isUpgrade = this.getByUUID(otherUUID).isUpgradeFor(other.getByUUID(otherUUID));
			if (!isUpgrade) {
				return false;
			}
		}
		
		return true;		
		
	}
	
	
	
	public List<BoltIssue> getIssues() {
		return Collections.unmodifiableList(issues);
	}
	
	public void addIssue(BoltIssue issue) {
		issues.add(issue);
	}
	

}
