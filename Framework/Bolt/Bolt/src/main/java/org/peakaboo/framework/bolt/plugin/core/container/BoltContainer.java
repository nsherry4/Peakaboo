package org.peakaboo.framework.bolt.plugin.core.container;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginCollection;

public interface BoltContainer<T extends BoltPlugin> extends PluginCollection<T> {

	String getSourcePath();
	String getSourceName();
	
	boolean isDeletable();
	boolean delete();
		
}
