package org.peakaboo.controller.mapper.settings;

import org.peakaboo.controller.mapper.MappingController;

import eventful.EventfulType;


public class MapSettingsController extends EventfulType<String>
{
		
	//components
	private MapFittingSettings 	mapFittings;	
	private MapViewSettings viewSettings;
	
	
	
	public MapSettingsController(MappingController map, MapViewSettings copyViewSettings)
	{
		
		mapFittings = new MapFittingSettings(map);
		mapFittings.addListener(this::updateListeners);
		
		if (copyViewSettings == null) {
			viewSettings = new MapViewSettings(map);
		} else {
			viewSettings = new MapViewSettings(map, copyViewSettings);
		}
		viewSettings.addListener(this::updateListeners);

	}


	public MapFittingSettings getMapFittings() {
		return mapFittings;
	}


	public MapViewSettings getView() {
		return viewSettings;
	}

}
