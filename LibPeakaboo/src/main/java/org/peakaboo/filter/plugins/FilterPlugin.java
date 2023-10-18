package org.peakaboo.filter.plugins;

import org.peakaboo.filter.model.Filter;
import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;

public interface FilterPlugin extends Filter, BoltJavaPlugin {

	default String getFilterUUID() {
		return this.pluginUUID();
	}
	
}
