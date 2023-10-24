package org.peakaboo.controller.session.v2;

import java.util.LinkedHashMap;
import java.util.Map;

import org.peakaboo.framework.druthers.DruthersStorable;
import org.peakaboo.framework.druthers.serialize.DruthersLoadException;
import org.peakaboo.framework.druthers.serialize.DruthersSerializer;

//TODO include the name here, but that will require carrying the name through the controller from file load time to session save time
public class SavedPlugin implements DruthersStorable {
	
	public String uuid;
	public Map<String, Object> settings;
	
	public SavedPlugin() {}
	
	public SavedPlugin(String uuid, Map<String, Object> settings) {
		this.uuid = uuid;
		this.settings = settings == null ? new LinkedHashMap<>() : new LinkedHashMap<>(settings);
	}
	public SavedPlugin(SavedPlugin other) {
		this(other.uuid, other.settings);	
	}

	public static SavedPlugin load(String yaml) throws DruthersLoadException {
		return DruthersSerializer.deserialize(yaml, false, SavedPlugin.class);
	}
	
}