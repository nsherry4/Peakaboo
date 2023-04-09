package org.peakaboo.filter.model;

import java.util.List;
import java.util.Map;

import org.peakaboo.filter.plugins.FilterPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;

/**
 * SerializedFilter holds a reference to a filter, and provides getters/setters for
 * the class and the serialized parameters. When this class is serialzied, the class
 * and parameters are exposed to the serializing agent. When this object is read back
 * in, the setters will receive a Class, and serialized parameters. Calling getFilter 
 * will then reconstruct the filter from that data. 
 * @author NAS
 *
 */
public class SerializedFilter {


	private Filter filter;
	
	//These values exist only to initialize the filter, not to be read from.
	private String uuidOrClazz;
	
	/*
	 * This type is deprecated and not for use. It remains only as a hint to the
	 * serializer to let it load older serialized filters
	 * 
	 * For removal in Peakaboo 7
	 */
	@Deprecated(since = "6", forRemoval = true)
	private List<Object> settings;
	
	
	private Map<String, Object> settingsMap;
	
	
	
	public SerializedFilter() {	}
	
	public SerializedFilter(Filter filter) {
		
		this.filter = filter;
		
	}


	//Only called by the (de)serializer
	public String getClazz() {
		//prefer to reference filters by plugin UUID, which it *should* always have
		if (filter instanceof BoltPlugin) {
			return ((BoltPlugin)filter).pluginUUID();
		}
		return filter.getClass().getName();
	}

	//Only called by the (de)serializer
	public void setClazz(String clazz) {
		this.uuidOrClazz = clazz;
	}

	//Only called by the (de)serializer
	public Map<String, Object> getSettingsMap() {
		return filter.getParameterGroup().serializeMap();
	}

	//Only called by the (de)serializer
	@Deprecated(since = "6", forRemoval = true)
	public void setSettings(List<Object> settings) {
		this.settings = settings;
		this.settingsMap = null;
	}

	//Only called by the (de)serializer
	public void setSettingsMap(Map<String, Object> settings) {
		this.settingsMap = settings;
		this.settings = null;
	}
	
	public Filter getFilter() {
		//If it already exists, just return it, otherwise build a filter
		if (filter != null) { return filter; }
			
		for (BoltPluginPrototype<? extends FilterPlugin> plugin : FilterPluginManager.system().getPlugins()) {
			if (
				plugin.getUUID().equals(uuidOrClazz) || 
				plugin.getImplementationClass().getName().equals(uuidOrClazz)
			) {
				filter = plugin.create();
				filter.initialize();
				try {
					/*
					 * We're moving from settings to settingsmap, and we want to continue supporting
					 * older session which load settings (not a map). We check if settingsMap is
					 * populated first, then fall back to checking if an older session has been
					 * loaded.
					 */
					if (this.settingsMap != null) {
						filter.getParameterGroup().deserializeMap(this.settingsMap);
					} else {
						filter.getParameterGroup().deserialize(this.settings);
					}
				} catch (IllegalArgumentException e) {
					throw new RuntimeException("Cannot build plugin: " + plugin.getName(), e);
				}
				return filter;
			}
		}
		throw new RuntimeException("Cannot find plugin " + uuidOrClazz);
	}

	
	
	
	
	
	
	
}
