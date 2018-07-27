package net.sciencestudio.bolt.plugin.core;

import java.util.List;

/**
 * Tracks a set of plugins and provides operations such as looking plugins 
 * up and getting new instances of all plugins.
 * @author NAS
 *
 * @param <T>
 */
public interface BoltPluginSet<T extends BoltPlugin> {

	
	List<BoltPluginController<? extends T>> getAll();

	
	BoltPluginController<? extends T> getByUUID(String uuid);
	
	
	default boolean hasUUID(String uuid) {
		return getByUUID(uuid) != null;
	}
	
	
	List<T> newInstances();
	
	
	void addPlugin(BoltPluginController<? extends T> plugin);
	
	default int size() {
		return getAll().size();
	}
	
}
