package org.peakaboo.app;

import java.util.Set;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;

/**
 * This class is a specialized plugin registry for Peakaboo plugins.
 * It extends the BoltPluginRegistry and restricts access to certain packages
 * that are specific to the Peakaboo application.
 * 
 * @param <P> The type of Peakaboo plugin.
 */
public abstract class PeakabooPluginRegistry<P extends BoltPlugin> extends BoltPluginRegistry<P> {
	
	protected PeakabooPluginRegistry(String slug) {
		super(slug);
	}

	@Override
	public Set<String> getRestrictedPackagePrefixes() {
		return Set.of(
				"org.peakaboo.ui",
				"org.peakaboo.app",
				"org.peakaboo.controller",
				"org.peakaboo.dataset",
				"org.peakaboo.display",
				"org.peakaboo.tier",
				"org.peakaboo.framework"
			);
	}


}
