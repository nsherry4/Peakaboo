package net.sciencestudio.bolt.plugin.core;

import java.util.List;

public interface BoltPluginSet<T extends BoltPlugin> {

	
	List<BoltPluginController<? extends T>> getAll();

	
	BoltPluginController<? extends T> getByUUID(String uuid);
	
	
	default boolean hasUUID(String uuid) {
		return getByUUID(uuid) != null;
	}
	
	
	List<T> newInstances();
	
	
	void addPlugin(BoltPluginController<? extends T> plugin);
	
	
}
