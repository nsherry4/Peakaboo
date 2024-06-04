package org.peakaboo.framework.bolt.plugin.core;

import java.util.List;
import java.util.Optional;

import org.peakaboo.framework.bolt.plugin.core.issue.BoltIssue;
import org.peakaboo.framework.bolt.plugin.core.loader.BoltLoader;

public interface PluginRegistry <P extends BoltPlugin> extends PluginCollection<P> {

	/**
	 * Gets a string containing only letters, numbers and dashes. This string identifies the type of plugins
	 */
	String getSlug();
	
	/**
	 * Provides a name for the kind of plugins managed by this manager
	 * @return
	 */
	String getInterfaceName();
	
	/**
	 * Provides a description for the kind of plugins managed by this manager
	 */
	String getInterfaceDescription();
	
	/**
	 * Adds a new {@link BoltLoader} to this registry. Adding the loader will allow
	 * this registry to access all of the plugins that it has loaded.
	 * 
	 * @param loader
	 */
	void addLoader(BoltLoader<P> loader);

	
	List<BoltIssue<P>> getIssues();

	String getAssetPath();

	void load();
	void clear();
	void reload();
	
	
	
	
	default Optional<P> fromSaved(SavedPlugin saved) {
		Optional<PluginDescriptor<P>> lookup = this.getByUUID(saved.uuid);
		if (lookup.isEmpty()) {
			return Optional.empty();
		}
		return lookup.get().create();
	}
	
	default PluginRegistry<P> getManager() {
		return this;
	}
	
}
