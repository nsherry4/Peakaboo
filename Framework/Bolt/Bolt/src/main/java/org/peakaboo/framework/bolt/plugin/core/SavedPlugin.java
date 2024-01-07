package org.peakaboo.framework.bolt.plugin.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.peakaboo.framework.druthers.DruthersStorable;
import org.peakaboo.framework.druthers.serialize.DruthersLoadException;
import org.peakaboo.framework.druthers.serialize.DruthersSerializer;

public class SavedPlugin implements DruthersStorable {
	
	public String uuid;
	public String name;
	public Map<String, Object> settings;
	public String version = "";
	
	public SavedPlugin() {}
	
	// Standard constructors
	public SavedPlugin(String uuid, String name, String version) {
		this(uuid, name, version, new HashMap<>());
	}
	public SavedPlugin(String uuid, String name, String version, Map<String, Object> settings) {
		this.uuid = uuid;
		this.settings = settings == null ? new LinkedHashMap<>() : new LinkedHashMap<>(settings);
		this.name = name;
		this.version = version;
	}
	
	// Copy constructors
	public SavedPlugin(SavedPlugin other) {
		this(other.uuid, other.name, other.version, other.settings);	
	}
	public SavedPlugin(SavedPlugin other, Map<String, Object> settings) {
		this(other.uuid, other.name, other.version, settings);	
	}
	
	// Plugin-serializing constructors
	public SavedPlugin(BoltPlugin plugin) {
		this(plugin.pluginUUID(), plugin.pluginName(), plugin.pluginVersion(), new HashMap<>());
	}
	public SavedPlugin(BoltPlugin plugin, Map<String, Object> settings) {
		this(plugin.pluginUUID(), plugin.pluginName(), plugin.pluginVersion(), settings);
	}
	
	
	public static SavedPlugin load(String yaml) throws DruthersLoadException {
		return DruthersSerializer.deserialize(yaml, false, SavedPlugin.class);
	}
	
}