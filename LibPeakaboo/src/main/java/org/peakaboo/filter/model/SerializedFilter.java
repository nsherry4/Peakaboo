package org.peakaboo.filter.model;

import java.util.List;

import org.peakaboo.filter.plugins.FilterPlugin;

import net.sciencestudio.bolt.plugin.core.BoltPluginPrototype;

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
	private String clazz;
	private List<Object> settings;
	
	
	
	public SerializedFilter() {	}
	
	public SerializedFilter(Filter filter) {
		
		this.filter = filter;
		
	}



	public String getClazz() {
		return filter.getClass().getName();
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public List<Object> getSettings() {
		return filter.getParameterGroup().serialize();
	}

	public void setSettings(List<Object> settings) {
		this.settings = settings;
	}

	public Filter getFilter() {
		if (filter != null) { return filter; }
			
		for (BoltPluginPrototype<? extends FilterPlugin> plugin : FilterPluginManager.SYSTEM.getPlugins().getAll()) {
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
