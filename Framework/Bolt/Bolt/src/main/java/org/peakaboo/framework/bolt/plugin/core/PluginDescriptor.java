package org.peakaboo.framework.bolt.plugin.core;

import java.util.Optional;

import org.peakaboo.framework.bolt.plugin.core.container.BoltContainer;

/**
 * A {@link PluginDescriptor} wraps a single plugin and allows it to be introspected and new instances created.
 * @author NAS
 *
 * @param <T>
 */
public interface PluginDescriptor<T extends BoltPlugin> {

	Class<? extends T> getImplementationClass();

	/**
	 * Gets the base class that all plugins of this type must implement or extend
	 */
	Class<T> getPluginClass();

	/**
	 * Create a new instance of the {@link BoltPlugin} described by this component
	 * @return
	 */
	Optional<T> create();

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
	 * Weight values are used to sort plugins. A higher value is a higher
	 * precedence. Values should range from 0 to 100 inclusive
	 */
	int getWeight();
	static final int WEIGHT_LOWEST = 2;
	static final int WEIGHT_LOWER = 10;
	static final int WEIGHT_LOW = 25;
	static final int WEIGHT_MEDIUM = 50;
	static final int WEIGHT_HIGH = 75;
	static final int WEIGHT_HIGHER = 90;
	static final int WEIGHT_HIGHEST = 98;
	
	/**
	 * A UUID uniquely identifying the plugin.
	 */
	String getUUID();

	BoltContainer<T> getContainer();

	PluginRegistry<T> getRegistry();
	
	/**
	 * Returns true if (and only if) the UUID of the other plugin matches 
	 * this one, and the version of this plugin is the same or greater than the other one 
	 * @param other the plugin to test against
	 * @return true if this plugin's version is newer or the same, false if this plugin version is older, or if the UUIDs don't match
	 */
	default boolean isUpgradeFor(PluginDescriptor<?> other) {
		if (!getUUID().equals(other.getUUID())) {
			return false;
		}
		int cmp = AlphaNumericComparitor.compareVersions(getVersion(), other.getVersion());
		if (cmp >= 0) {
			//this plugin is a newer (or same) version of the given one, so it's an upgrade
			return true;
		} else {
			//other is the newer version
			return false;
		}
	}
	
	default boolean isNewerThan(PluginDescriptor<?> other) {
		if (!getUUID().equals(other.getUUID())) {
			return false;
		}
		int cmp = AlphaNumericComparitor.compareVersions(getVersion(), other.getVersion());
		if (cmp > 0) {
			//this plugin is a newer version of the given one, so it's an upgrade
			return true;
		} else {
			//this is an older (or same) version
			return false;
		}
	}
	
	/**
	 * Returns a SavedPlugin object describing the plugin represented by this
	 * {@link PluginDescriptor}. The SavedPlugin will be based on a freshly created
	 * instance of the plugin.
	 */
	default Optional<SavedPlugin> save() {
		return create().map(SavedPlugin::new);	
	}	
	
}