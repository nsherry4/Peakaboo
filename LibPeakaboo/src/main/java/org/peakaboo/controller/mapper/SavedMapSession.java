package org.peakaboo.controller.mapper;

import org.peakaboo.controller.mapper.dimensions.SavedMapDimensionsSession;
import org.peakaboo.controller.mapper.filtering.SavedMapFilteringSession;
import org.peakaboo.controller.mapper.fitting.SavedMapFittingSession;
import org.peakaboo.controller.mapper.settings.SavedMapSettingsController;
import org.peakaboo.framework.druthers.DruthersStorable;

public class SavedMapSession extends DruthersStorable {

	public SavedMapDimensionsSession dimensions;
	public SavedMapSettingsController settings;
	public SavedMapFilteringSession filters;
	public SavedMapFittingSession fittings;
	
	
	public void loadInto(MappingController map) {
		
		dimensions.loadInto(map.getUserDimensions());
		settings.loadInto(map.getSettings());
		filters.loadInto(map.getFiltering());
		fittings.loadInto(map.getFitting());
		
	}
	
	public SavedMapSession storeFrom(MappingController map) {
		
		dimensions = new SavedMapDimensionsSession().storeFrom(map.getUserDimensions());
		settings = new SavedMapSettingsController().storeFrom(map.getSettings());
		filters = new SavedMapFilteringSession().storeFrom(map.getFiltering());
		fittings = new SavedMapFittingSession().storeFrom(map.getFitting());
		
		return this;
	}
	
}
