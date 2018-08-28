package peakaboo.controller.plotter.view;


public class PersistentViewModel {

	public boolean				showElementFitTitles;
	public boolean				showElementFitMarkers;
	public boolean				showElementFitIntensities;
	public boolean				showIndividualFittings;
	public boolean				showPlotTitle;
	public boolean				showAxes;
	
	public boolean				monochrome;
	public boolean				consistentScale = true;
	
	
	public PersistentViewModel() {
		showElementFitTitles = true;
		showAxes = true;
	}
	
	public PersistentViewModel(PersistentViewModel copy) {

		showElementFitTitles = copy.showElementFitTitles;
		showElementFitMarkers = copy.showElementFitMarkers;
		showElementFitIntensities = copy.showElementFitIntensities;
		showIndividualFittings = copy.showIndividualFittings;
		showPlotTitle = copy.showPlotTitle;
		showAxes = copy.showAxes;
		
		monochrome = copy.monochrome;
		consistentScale = copy.consistentScale;
		
	}
	
}
