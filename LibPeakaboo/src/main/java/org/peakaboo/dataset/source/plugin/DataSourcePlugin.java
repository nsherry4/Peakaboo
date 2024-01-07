package org.peakaboo.dataset.source.plugin;

import java.util.HashMap;

import org.peakaboo.dataset.source.model.DataSource;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.bolt.plugin.java.SavedPlugin;

public interface DataSourcePlugin extends DataSource, BoltJavaPlugin {
	//TODO implement save override, look for where we do it now?
	
	default SavedPlugin save() {
		//return new SavedPlugin(pluginUUID(), pluginName(), getParametes());
		return new SavedPlugin(pluginUUID(), pluginName(), new HashMap<>());
	}
	
}
