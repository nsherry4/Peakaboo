package org.peakaboo.framework.bolt.plugin.core;

public interface BoltPlugin {

	/**
	 * Returns true if this plugin is able to be used, false otherwise
	 * <br /><br />
	 * There may be cases where plugins have certain requirements which 
	 * must be met in order to function properly in a given situation. If
	 * a plugin returns false for this method, it will not be exposed 
	 * to the software using the plugins.
	 */
	default boolean pluginEnabled() {
		return true;
	}

	/**
	 * A short, descriptive name for this plugin.
	 */
	String pluginName();

	/**
	 * A longer description of what this plugin is and what it does.
	 * @return
	 */
	String pluginDescription();

	/**
	 * A version string for this plugin.
	 */
	String pluginVersion();
	
	
	/**
	 * Static UUID uniquely identifying the plugin
	 */
	String pluginUUID();

}