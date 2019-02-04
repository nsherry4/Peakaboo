package org.peakaboo.controller.mapper;

import org.peakaboo.common.YamlSerializer;
import org.peakaboo.controller.mapper.dimensions.SavedMapDimensionsSession;
import org.peakaboo.controller.mapper.filtering.SavedMapFilteringSession;
import org.peakaboo.controller.mapper.settings.SavedMapSettingsController;

public class SavedMapSession {

	public SavedMapDimensionsSession dimensions;
	public SavedMapSettingsController settings;
	public SavedMapFilteringSession filters;
	
	
	/**
	 * Decodes a serialized data object from yaml
	 */
	public static SavedMapSession deserialize(String yaml) {
		return YamlSerializer.deserialize(yaml);
	}
	/**
	 * Encodes the serialized data as yaml
	 */
	public String serialize() {
		return YamlSerializer.serialize(this);
	}
	
	
	public void loadInto(MappingController map) {
		
		dimensions.loadInto(map.getUserDimensions());
		settings.loadInto(map.getSettings());
		filters.loadInto(map.getFiltering());
		
	}
	
	public SavedMapSession storeFrom(MappingController map) {
		
		dimensions = new SavedMapDimensionsSession().storeFrom(map.getUserDimensions());
		settings = new SavedMapSettingsController().storeFrom(map.getSettings());
		filters = new SavedMapFilteringSession().storeFrom(map.getFiltering());
		
		return this;
	}
	
}
