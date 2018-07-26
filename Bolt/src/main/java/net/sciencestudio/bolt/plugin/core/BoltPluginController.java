package net.sciencestudio.bolt.plugin.core;

import java.net.URL;

/**
 * A BoltPluginController wraps a single plugin and allows it to be managed and introspected.
 * @author NAS
 *
 * @param <T>
 */
public interface BoltPluginController<T extends BoltPlugin> {

	Class<? extends T> getImplementationClass();

	/**
	 * Gets the base class that all plugins of this type must implement or extend
	 */
	Class<T> getPluginClass();

	T create();

	boolean isEnabled();

	/**
	 * Returns an instance of this plugin which is to be used for reference only. 
	 * Do not use this instance of the plugin directly.
	 */
	T getReferenceInstance();
	
	/**
	 * A short, descriptive name for this plugin. If the plugin cannot be loaded, returns null.
	 */
	String getName();

	/**
	 * A longer description of what this plugin is and what it does. If the plugin cannot be loaded, returns null.
	 * @return
	 */
	String getDescription();

	/**
	 * A version string for this plugin. If the plugin cannot be loaded, returns null.
	 */
	String getVersion();
	
	/**
	 * A UUID uniquely identifying the plugin.
	 */
	String getUUID();

	URL getSource();

}