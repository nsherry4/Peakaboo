package org.peakaboo.mapping.filter.model;

import org.peakaboo.framework.bolt.plugin.core.SavedPlugin;
import org.peakaboo.mapping.filter.plugin.MapFilterPlugin;

/**
 * Serialized map filter state: the plugin reference, its serialized
 * parameters, and its enabled flag -- collected in the constructor. We don't
 * track the original filter as that leads to window-to-window interference.
 * Instead, {@link #buildFilter()} builds a fresh instance on every call
 * @author NAS
 *
 */
public class SerializedMapFilter {

	// A SavedPlugin gets the filter by its UUID
	private SavedPlugin plugin;
	private boolean enabled = true;


	public SerializedMapFilter() {	}

	public SerializedMapFilter(MapFilter filter) {
		if (!(filter instanceof MapFilterPlugin p)) {
			throw new IllegalArgumentException("Map filter " + filter.getClass().getName() + " is not a plugin");
		}
		this.plugin = p.save();
		this.enabled = filter.isEnabled();
	}

	public SavedPlugin getPlugin() {
		return plugin;
	}

	public void setPlugin(SavedPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	// Builds a new MapFilter from the serialized state
	public MapFilter buildFilter() {
		// fromSaved() only looks the plugin up by UUID and creates it, but does not
		// initialize or apply settings, so we do that manually
		MapFilter filter = MapFilterRegistry.system().fromSaved(plugin)
				.orElseThrow(() -> new RuntimeException("Cannot find map filter plugin " + plugin.uuid));

		filter.initialize();
		if (plugin.settings != null) {
			filter.getParameterGroup().deserialize(plugin.settings);
		}
		filter.setEnabled(enabled);
		return filter;
	}

}
