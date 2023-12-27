package org.peakaboo.mapping.filter.plugin;

import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.mapping.filter.model.MapFilter;

public interface MapFilterPlugin extends MapFilter, BoltJavaPlugin {

	default String pluginName() {
		return getFilterName();
	}

	default String pluginDescription() {
		return getFilterDescription();
	}

	
}
