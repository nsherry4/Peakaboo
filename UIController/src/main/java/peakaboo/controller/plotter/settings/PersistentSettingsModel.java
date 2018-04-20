package peakaboo.controller.plotter.settings;


public class PersistentSettingsModel {

	public boolean				showElementFitTitles;
	public boolean				showElementFitMarkers;
	public boolean				showElementFitIntensities;
	public boolean				showIndividualFittings;
	public boolean				showPlotTitle;
	public boolean				showAxes;
	
	public boolean				monochrome;
	
	
	public PersistentSettingsModel() {
		showElementFitTitles = true;
		showAxes = true;
	}
	
	public PersistentSettingsModel(PersistentSettingsModel copy) {

		showElementFitTitles = copy.showElementFitTitles;
		showElementFitMarkers = copy.showElementFitMarkers;
		showElementFitIntensities = copy.showElementFitIntensities;
		showIndividualFittings = copy.showIndividualFittings;
		showPlotTitle = copy.showPlotTitle;
		showAxes = copy.showAxes;
		
		monochrome = copy.monochrome;
		
	}
	
}
