package net.sciencestudio.bolt.plugin.core.container;

import net.sciencestudio.bolt.plugin.core.BoltPlugin;
import net.sciencestudio.bolt.plugin.core.BoltPluginSet;

public interface BoltContainer<T extends BoltPlugin> {

	String getSourcePath();
	String getSourceName();
	
	boolean isDeletable();
	boolean delete();
	
	BoltPluginSet<T> getPlugins();
	
}
