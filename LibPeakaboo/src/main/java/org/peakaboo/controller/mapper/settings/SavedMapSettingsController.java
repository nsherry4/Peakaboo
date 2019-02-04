package org.peakaboo.controller.mapper.settings;

public class SavedMapSettingsController {

	public boolean	drawCoordinates		= true;
	public boolean	drawSpectrum		= true;
	public boolean	drawTitle			= true;
	public boolean 	drawScaleBar		= true;
	public boolean	drawDataSetTitle	= false;
	
	public int		spectrumSteps		= 15;
	public boolean	contour				= false;
	public boolean	monochrome			= false;

	public void loadInto(MapSettingsController controller) {
		controller.setShowCoords(drawCoordinates);
		controller.setShowSpectrum(drawSpectrum);
		controller.setShowTitle(drawTitle);
		controller.setShowScaleBar(drawScaleBar);
		controller.setShowDatasetTitle(drawDataSetTitle);
		controller.setSpectrumSteps(spectrumSteps);
		controller.setContours(contour);
		controller.setMonochrome(monochrome);
	}
	
	public SavedMapSettingsController storeFrom(MapSettingsController controller) {
		
		drawCoordinates = controller.getShowCoords();
		drawSpectrum = controller.getShowSpectrum();
		drawTitle = controller.getShowTitle();
		drawScaleBar = controller.getShowScaleBar();
		drawDataSetTitle = controller.getShowDatasetTitle();
		spectrumSteps = controller.getSpectrumSteps();
		contour = controller.getContours();
		monochrome = controller.getMonochrome();
		
		return this;
	}
	
}
