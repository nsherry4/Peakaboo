package org.peakaboo.framework.bolt.plugin.core.container;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginCollection;

public interface BoltContainer<T extends BoltPlugin> extends PluginCollection<T> {

	/**
	 * Returns a String which represents the fully qualified path for the container, including filename
	 */
	String getSourcePath();
	String getSourceName();

	boolean isDeletable();
	boolean delete();

	/**
	 * Closes any resources held by this container (e.g., classloaders, file handles) without
	 * deleting the underlying source. This should be called when the container is being discarded
	 * but the source file should remain (e.g., during a reload).
	 *
	 * Default implementation does nothing. Containers that hold resources should override this.
	 */
	default void close() {
		// Default: no resources to close
	}

}
