package org.peakaboo.framework.bolt.plugin.core;

/**
 * PluginPreset defines how a component like a plugin Registry can provide a default plugin
 */
public interface PluginPreset<P extends BoltPlugin> {

	/**
	 * Returns a {@link PluginDescriptor} of the preset choice of implementation for
	 * this plugin interface, if one has been set.
	 */
	PluginDescriptor<P> getPreset();
	
	default P getPresetInstance() {
		// Don't use Optional and force all callers to check to make sure we loaded the
		// fallback/preset/default implementation. If we can't even load the fallback,
		// we can just accept defeat and throw an exception
		return getPreset().create().orElseThrow();
	}
	
}
