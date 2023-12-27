package org.peakaboo.mapping.filter.model;

import java.util.Map;

import org.peakaboo.framework.bolt.plugin.core.BoltPluginPrototype;
import org.peakaboo.mapping.filter.plugin.MapFilterPlugin;

/**
 * SerializedFilter holds a reference to a filter, and provides getters/setters for
 * the class and the serialized parameters. When this class is serialzied, the class
 * and parameters are exposed to the serializing agent. When this object is read back
 * in, the setters will receive a Class, and serialized parameters. Calling getFilter 
 * will then reconstruct the filter from that data. 
 * @author NAS
 *
 */
public class SerializedMapFilter {


	private MapFilter filter;
	
	//These values exist only to initialize the filter, not to be read from.
	private String clazz;
	private Map<String, Object> settings;
	
	
	
	public SerializedMapFilter() {	}
	
	public SerializedMapFilter(MapFilter filter) {
		
		this.filter = filter;
		
	}



	public String getClazz() {
		return filter.getClass().getName();
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public Map<String, Object> getSettings() {
		return filter.getParameterGroup().serialize();
	}

	public void setSettings(Map<String, Object> settings) {
		this.settings = settings;
	}

	public MapFilter getFilter() {
		if (filter != null) { return filter; }
			
		for (BoltPluginPrototype<? extends MapFilterPlugin> plugin : MapFilterRegistry.system().getPlugins()) {
			if (plugin.getImplementationClass().getName().equals(clazz)) {
				filter = plugin.create();
				filter.initialize();
				filter.getParameterGroup().deserialize(settings);
				return filter;
			}
		}
		throw new RuntimeException("Cannot find plugin " + clazz);
	}

	
	
	
	
	
	
	
}
