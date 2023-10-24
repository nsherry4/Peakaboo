package org.peakaboo.controller.session.v2;

import java.util.LinkedHashMap;
import java.util.Map;

//TODO include the name here, but that will require carrying the name through the controller from file load time to session save time
public class SavedPlugin {
	
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

}