package org.peakaboo.controller.mapper.fitting;

import org.peakaboo.display.map.MapScaleMode;

public class SavedMapFittingSession {

	private String mapScaleMode = null;
	
	public SavedMapFittingSession storeFrom(MapFittingController fitting) {
		this.mapScaleMode = fitting.getMapScaleMode().toString();
		
		return this;
	}

	public void loadInto(MapFittingController fitting) {
		fitting.setMapScaleMode(MapScaleMode.valueOf(this.mapScaleMode));
	}

}
