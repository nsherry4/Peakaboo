package org.peakaboo.mapping.filter.plugin;

import org.peakaboo.framework.bolt.plugin.java.BoltJavaPlugin;
import org.peakaboo.framework.bolt.plugin.java.SavedPlugin;
import org.peakaboo.mapping.filter.model.MapFilter;

public interface MapFilterPlugin extends MapFilter, BoltJavaPlugin {

	default String pluginName() {
		return getFilterName();
	}

	default String pluginDescription() {
		return getFilterDescription();
	}

	default SavedPlugin save() {
		return new SavedPlugin(pluginUUID(), pluginName(), getParameterGroup().serialize());
	}
}
