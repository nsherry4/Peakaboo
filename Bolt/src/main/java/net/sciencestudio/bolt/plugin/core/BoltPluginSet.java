package net.sciencestudio.bolt.plugin.core;

import java.util.List;
import java.util.stream.Collectors;

import net.sciencestudio.bolt.plugin.core.issue.BoltIssue;

/**
 * Tracks a set of plugins and provides operations such as looking plugins 
 * up and getting new instances of all plugins.
 * @author NAS
 *
 * @param <T>
 */
public interface BoltPluginSet<T extends BoltPlugin> {

	
	List<BoltPluginPrototype<? extends T>> getAll();

	
	BoltPluginPrototype<? extends T> getByUUID(String uuid);
	
	
	default boolean hasUUID(String uuid) {
		return getByUUID(uuid) != null;
	}
	
	
	List<T> newInstances();
	
	
	void addPlugin(BoltPluginPrototype<? extends T> plugin);

	
	default int size() {
		return getAll().size();
	}
	
	List<BoltIssue> getIssues();
	void addIssue(BoltIssue issue);
	
	/**
	 * Checks this set against the given one to determine if this 
	 * set is a proper upgrade for the given set. This is only true
	 * if this set contains the other set, and all matching elements 
	 * have version numbers greater than or equal to their 
	 * counterparts in the other set. 
	 * @param other the other set to compare against
	 * @return true if this set is a proper upgrade for the other, false otherwise
	 */
	default boolean isUpgradeFor(BoltPluginSet<T> other) {
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
	
}
