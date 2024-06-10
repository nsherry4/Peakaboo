package org.peakaboo.controller.mapper.settings;

import org.peakaboo.framework.cyclops.visualization.palette.Gradients;

public class SavedMapSettingsController {

	public boolean	drawCoordinates		= true;
	public boolean	drawSpectrum		= true;
	public boolean	drawTitle			= true;
	public boolean 	drawScaleBar		= true;
	public boolean	drawDataSetTitle	= false;
	
	public int		spectrumSteps		= 15;
	public boolean	contour				= false;
	public String   colourPalette       = "";

	public void loadInto(MapSettingsController controller) {
		controller.setShowCoords(drawCoordinates);
		controller.setShowSpectrum(drawSpectrum);
		controller.setShowTitle(drawTitle);
		controller.setShowScaleBar(drawScaleBar);
		controller.setShowDatasetTitle(drawDataSetTitle);
		controller.setSpectrumSteps(spectrumSteps);
		controller.setContours(contour);
		controller.setColourGradient(Gradients.forName(colourPalette).orElse(Gradients.DEFAULT));
		
	}
	
	public SavedMapSettingsController storeFrom(MapSettingsController controller) {
		
		drawCoordinates = controller.getShowCoords();
		drawSpectrum = controller.getShowSpectrum();
		drawTitle = controller.getShowTitle();
		drawScaleBar = controller.getShowScaleBar();
		drawDataSetTitle = controller.getShowDatasetTitle();
		spectrumSteps = controller.getSpectrumSteps();
		contour = controller.getContours();
		colourPalette = controller.getColourGradient().getName();
		
		return this;
	}
	
}
