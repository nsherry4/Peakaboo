package org.peakaboo.controller.plotter.view;


public class PersistentViewModel {

	public boolean				showElementFitMarkers;
	public boolean				showElementFitIntensities;
	public boolean				showIndividualFittings;
	
	public boolean				monochrome;
	public boolean				consistentScale;
	
	
	public PersistentViewModel() {
		showElementFitMarkers = true;
		showIndividualFittings = true;
		consistentScale = true;
	}
	
	public PersistentViewModel(PersistentViewModel copy) {

		showElementFitMarkers = copy.showElementFitMarkers;
		showElementFitIntensities = copy.showElementFitIntensities;
		showIndividualFittings = copy.showIndividualFittings;
		
		monochrome = copy.monochrome;
		consistentScale = copy.consistentScale;
		
	}
	
}
