package org.peakaboo.dataset.source.plugin;

import java.util.HashMap;

import org.peakaboo.dataset.source.model.DataSource;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.bolt.plugin.java.SavedPlugin;

public interface DataSourcePlugin extends DataSource, BoltJavaPlugin {
	
	default SavedPlugin save() {
		// DataSource settings must be stashed and added by loader
		return new SavedPlugin(pluginUUID(), pluginName(), new HashMap<>());
	}
	
}
