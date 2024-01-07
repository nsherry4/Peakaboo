package org.peakaboo.dataset.source.plugin;

import java.util.HashMap;

import org.peakaboo.dataset.source.model.DataSource;
import org.peakaboo.framework.bolt.plugin.core.SavedPlugin;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;

public interface DataSourcePlugin extends DataSource, BoltJavaPlugin {
	
	default SavedPlugin save() {
		// DataSource settings must be remembered and added to this by data loader
		return new SavedPlugin(this, new HashMap<>());
	}
	
}
