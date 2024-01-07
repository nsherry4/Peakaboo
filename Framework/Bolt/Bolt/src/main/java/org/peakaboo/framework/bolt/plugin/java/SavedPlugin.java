package org.peakaboo.framework.bolt.plugin.java;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.peakaboo.framework.druthers.DruthersStorable;
import org.peakaboo.framework.druthers.serialize.DruthersLoadException;
import org.peakaboo.framework.druthers.serialize.DruthersSerializer;

//TODO include the name here, but that will require carrying the name through the controller from file load time to session save time
public class SavedPlugin implements DruthersStorable {
	
	public String uuid;
	public String name;
	public Map<String, Object> settings;
	
	public SavedPlugin() {}
	
	public SavedPlugin(String uuid, String name) {
		this(uuid, name, new HashMap<>());
	}
	
	public SavedPlugin(String uuid, String name, Map<String, Object> settings) {
		this.uuid = uuid;
		this.settings = settings == null ? new LinkedHashMap<>() : new LinkedHashMap<>(settings);
		this.name = name;
	}
	public SavedPlugin(SavedPlugin other) {
		this(other.uuid, other.name, other.settings);	
	}

	public SavedPlugin(BoltJavaPlugin plugin) {
		this(plugin.pluginUUID(), plugin.pluginName(), new HashMap<>());
	}
	
	public static SavedPlugin load(String yaml) throws DruthersLoadException {
		return DruthersSerializer.deserialize(yaml, false, SavedPlugin.class);
	}
	
}