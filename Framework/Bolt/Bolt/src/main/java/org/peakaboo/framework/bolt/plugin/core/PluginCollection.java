package org.peakaboo.framework.bolt.plugin.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;

/**
 * Interface for exposing a set of {@link BoltPlugin}s and {@link BoltIssue}s
 * in a read-only way.
 */
public interface PluginCollection<T extends BoltPlugin> extends Iterable<PluginDescriptor<? extends T>> {

	List<PluginDescriptor<? extends T>> getPlugins();
	
	default Iterator<PluginDescriptor<? extends T>> iterator() {
		return getPlugins().iterator();
	}
	
	List<BoltIssue<? extends T>> getIssues();
	
	default List<T> newInstances() {
		List<T> insts = getPlugins().stream().map(p -> p.create()).collect(Collectors.toList());
		Collections.sort(insts, (f1, f2) -> f1.pluginName().compareTo(f1.pluginName()));
		return insts;
	}
	
	PluginRegistry<T> getManager();
	
	default PluginDescriptor<? extends T> getByUUID(String uuid) {
		for (PluginDescriptor<? extends T> plugin : getPlugins()) {
			if (plugin.getUUID().equals(uuid)) {
				return plugin;
			}
		}
		return null;
	}
	
	default Optional<PluginDescriptor<? extends T>> getByClass(Class<? extends T> cls) {
		synchronized(this) {
			for (var plugin : getPlugins()) {
				if (plugin.getReferenceInstance().getClass().equals(cls)) {
					return Optional.of(plugin);
				}
			}
			return Optional.empty();
		}
	}
	
	default boolean hasUUID(String uuid) {
		return getByUUID(uuid) != null;
	}
	
	default int size() {
		return getPlugins().size();
	}
	
	default boolean isEmpty() {
		return size() == 0;
	}
	
	/**
	 * Checks this plugin collection against the given one to determine if this
	 * collection is a proper upgrade for the supplied collection. This is only true
	 * if this collection contains the other collection, and all matching elements
	 * have version numbers greater than or equal to their counterparts in the other
	 * collection.
	 * 
	 * @param other the other collection to compare against
	 * @return true if this collection is a proper upgrade for the other, false otherwise
	 */
	default boolean isUpgradeFor(PluginCollection<? extends T> other) {
		//get all the UUIDs from the other plugin set
		List<String> otherUUIDs = other.getPlugins().stream().map(p -> p.getUUID()).collect(Collectors.toList());
				
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
