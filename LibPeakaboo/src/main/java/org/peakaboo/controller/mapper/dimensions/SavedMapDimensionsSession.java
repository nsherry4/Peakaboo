package org.peakaboo.controller.mapper.dimensions;

public class SavedMapDimensionsSession {

	public int userWidth, userHeight;
	
	public void loadInto(MapDimensionsController controller) {
		
		controller.setUserDataWidth(userWidth);
		controller.setUserDataHeight(userHeight);
		
	}
	
	public SavedMapDimensionsSession storeFrom(MapDimensionsController controller) {
		
		userHeight = controller.getUserDataHeight();
		userWidth = controller.getUserDataWidth();
		
		return this;
	}
	
}
