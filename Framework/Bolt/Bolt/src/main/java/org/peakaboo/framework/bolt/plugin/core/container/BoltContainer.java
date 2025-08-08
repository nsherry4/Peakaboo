package org.peakaboo.framework.bolt.plugin.core.container;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginCollection;

public interface BoltContainer<T extends BoltPlugin> extends PluginCollection<T> {

	/**
	 * Returns a String which represents the fully qualified path for the container, including filename 
	 */
	String getSourcePath();
	String getSourceName();
	
	boolean isDeletable();
	boolean delete();
		
}
