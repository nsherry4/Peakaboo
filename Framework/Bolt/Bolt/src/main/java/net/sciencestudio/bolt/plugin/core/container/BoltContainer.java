package net.sciencestudio.bolt.plugin.core.container;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.BoltPluginCollection;

public interface BoltContainer<T extends BoltPlugin> extends BoltPluginCollection<T> {

	String getSourcePath();
	String getSourceName();
	
	boolean isDeletable();
	boolean delete();
		
}
