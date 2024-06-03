package org.peakaboo.filter.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;

/**
 * SerializedFilter holds a reference to a filter, and provides getters/setters for
 * the class and the serialized parameters. When this class is serialzied, the class
 * and parameters are exposed to the serializing agent. When this object is read back
 * in, the setters will receive a Class, and serialized parameters. Calling getFilter 
 * will then reconstruct the filter from that data. 
 * @author NAS
 *
 */
@Deprecated(since = "6", forRemoval = true)
public class SerializedFilterV1 {


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
	
	
	@Deprecated(since = "6", forRemoval = true)
	public SerializedFilterV1() {	}
	
	@Deprecated(since = "6", forRemoval = true)
	public SerializedFilterV1(Filter filter) {
		
		this.filter = filter;
		
	}


	//Only called by the (de)serializer
	@Deprecated(since = "6", forRemoval = true)
	public String getClazz() {
		//prefer to reference filters by plugin UUID, which it *should* always have
		if (filter instanceof BoltPlugin) {
			return ((BoltPlugin)filter).pluginUUID();
		}
		return filter.getClass().getName();
	}

	//Only called by the (de)serializer
	@Deprecated(since = "6", forRemoval = true)
	public void setClazz(String clazz) {
		this.uuidOrClazz = clazz;
	}

	//Only called by the (de)serializer
	@Deprecated(since = "6", forRemoval = true)
	public Map<String, Object> getSettingsMap() {
		return filter.getParameterGroup().serialize();
	}

	//Only called by the (de)serializer
	@Deprecated(since = "6", forRemoval = true)
	public void setSettings(List<Object> settings) {
		this.settings = settings;
		this.settingsMap = null;
	}

	//Only called by the (de)serializer
	@Deprecated(since = "6", forRemoval = true)
	public void setSettingsMap(Map<String, Object> settings) {
		this.settingsMap = settings;
		this.settings = null;
	}
	
	@Deprecated(since = "6", forRemoval = true)
	public Optional<Filter> getFilter(List<String> errors) {
		//If it already exists, just return it, otherwise build a filter
		if (filter != null) { return Optional.of(filter); }
			
		for (PluginDescriptor<Filter> plugin : FilterRegistry.system().getPlugins()) {
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
						filter.getParameterGroup().deserialize(this.settingsMap);
					} else {
						filter.getParameterGroup().deserialize(this.settings);
					}
				} catch (IllegalArgumentException e) {
					var msg = "Cannot restore settings for " + plugin.getName();
					errors.add(msg);
					PeakabooLog.get().log(Level.WARNING, msg, e);
				}
				return Optional.of(filter);
			}
		}
		var msg = "Cannot restore plugin " + uuidOrClazz;
		errors.add(msg);
		PeakabooLog.get().log(Level.WARNING, msg);
		return Optional.empty();
	}

	
	
	
	
	
	
	
}
