package org.peakaboo.framework.bolt.plugin.core;

public interface PluginPreset<P extends BoltPlugin> {

	/**
	 * Returns a {@link PluginDescriptor} of the preset choice of implementation for
	 * this plugin interface, if one has been set.
	 */
	PluginDescriptor<P> getPreset();
	
	default P getPresetInstance() {
		return getPreset().create();
	}
	
}
