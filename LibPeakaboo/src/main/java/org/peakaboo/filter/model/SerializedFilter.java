package org.peakaboo.filter.model;

import java.util.List;

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
	private List<Object> settings;
	
	
	
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
	public List<Object> getSettings() {
		return filter.getParameterGroup().serialize();
	}

	//Only called by the (de)serializer
	public void setSettings(List<Object> settings) {
		this.settings = settings;
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
				filter.getParameterGroup().deserialize(settings);
				return filter;
			}
		}
		throw new RuntimeException("Cannot find plugin " + uuidOrClazz);
	}

	
	
	
	
	
	
	
}
