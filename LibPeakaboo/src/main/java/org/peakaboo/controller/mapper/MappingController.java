package org.peakaboo.controller.mapper;



import java.util.List;

import org.peakaboo.controller.mapper.dimensions.MapDimensionsController;
import org.peakaboo.controller.mapper.filtering.MapFilteringController;
import org.peakaboo.controller.mapper.fitting.MapFittingController;
import org.peakaboo.controller.mapper.rawdata.RawDataController;
import org.peakaboo.controller.mapper.selection.MapSelectionController;
import org.peakaboo.controller.mapper.settings.MapSettingsController;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.settings.SavedSession;
import org.peakaboo.datasource.model.internal.CroppedDataSource;
import org.peakaboo.datasource.model.internal.SelectionDataSource;
import org.peakaboo.display.map.MapRenderData;
import org.peakaboo.display.map.MapRenderSettings;
import org.peakaboo.display.map.MapScaleMode;

import cyclops.Coord;
import eventful.EventfulType;


public class MappingController extends EventfulType<String>
{
	
	private static final int SPECTRUM_HEIGHT = 15;

	public enum UpdateType
	{
		DATA_SIZE, DATA_OPTIONS, DATA, UI_OPTIONS, AREA_SELECTION, POINT_SELECTION, FILTER;
	}
	
	
	public 	RawDataController		rawDataController;
	private MapSettingsController	settingsController;
	private MapFilteringController 	filteringController;
	private MapSelectionController	selectionController;
	private MapDimensionsController dimensionsController;
	private MapFittingController 	fittingController;
	
	private PlotController			plotcontroller;
	
	
	/**
	 * This constructor copies the user preferences from the map,
	 * and directly references the map data
	 * @param copy
	 * @param plotcontroller
	 */
	public MappingController(RawDataController rawDataController, MapSettingsController copyViewSettings, MapDimensionsController copyUserDimensions, PlotController plotcontroller)
	{
		this.plotcontroller = plotcontroller;
		
		this.rawDataController = rawDataController;
		this.rawDataController.addListener(this::updateListeners);
		
		this.filteringController = new MapFilteringController(this);
		this.filteringController.addListener(this::updateListeners);
		
		this.selectionController = new MapSelectionController(this);
		this.selectionController.addListener(this::updateListeners);
		
		this.settingsController = new MapSettingsController(this, copyViewSettings);		
		this.settingsController.addListener(this::updateListeners);		
		
		this.fittingController = new MapFittingController(this);
		this.fittingController.addListener(this::updateListeners);
		
		
		this.dimensionsController = new MapDimensionsController(this, copyUserDimensions);	
		this.dimensionsController.addListener(this::updateListeners);
		
	}
	

	public MapSettingsController getSettings() {
		return settingsController;
	}
	
	public MapFilteringController getFiltering() {
		return filteringController;
	}

	public MapSelectionController getSelection() {
		return selectionController;
	}
	
	public MapDimensionsController getUserDimensions() {
		return dimensionsController;
	}

	public MapFittingController getFitting() {
		return fittingController;
	}
	
	public CroppedDataSource getDataSourceForSubset(Coord<Integer> cstart, Coord<Integer> cend)
	{
		return plotcontroller.data().getDataSourceForSubset(getUserDimensions().getUserDataWidth(), getUserDimensions().getUserDataHeight(), cstart, cend);
	}

	public SelectionDataSource getDataSourceForSubset(List<Integer> points)
	{
		return plotcontroller.data().getDataSourceForSubset(points);
	}
	
	
	public SavedSession getSavedSettings() {
		return plotcontroller.getSavedSettings();
	}
	
	
	public MapRenderSettings getRenderSettings() {
		MapRenderSettings settings = new MapRenderSettings();
		settings.userDataWidth = this.getUserDimensions().getUserDataWidth(); 
		settings.userDataHeight = this.getUserDimensions().getUserDataHeight();
		settings.filteredDataWidth = this.getFiltering().getFilteredDataWidth();
		settings.filteredDataHeight = this.getFiltering().getFilteredDataHeight();
		
		settings.showDatasetTitle = this.settingsController.getShowDatasetTitle();
		settings.datasetTitle = this.rawDataController.getDatasetTitle();
		settings.showScaleBar = this.settingsController.getShowScaleBar();
		settings.showMapTitle = this.settingsController.getShowTitle();
		settings.mapTitle = this.getFitting().mapLongTitle();
		
		settings.scalemode = this.getFitting().getMapScaleMode();
		settings.monochrome = this.settingsController.getMonochrome();
		settings.contours = this.settingsController.getContours();
		settings.contourSteps = this.settingsController.getSpectrumSteps();
		
		settings.mode = this.getFitting().getMapDisplayMode();
				
		settings.drawCoord = this.settingsController.getDrawCoords();
		settings.coordLoXLoY = this.getSettings().getLoXLoYCoord();
		settings.coordHiXLoY = this.getSettings().getHiXLoYCoord();
		settings.coordLoXHiY = this.getSettings().getLoXHiYCoord();
		settings.coordHiXHiY = this.getSettings().getHiXHiYCoord();
		settings.physicalUnits = this.rawDataController.getRealDimensionUnits();
		settings.physicalCoord = this.rawDataController.getRealDimensions() != null;
		
		settings.showSpectrum = this.settingsController.getShowSpectrum();
		settings.spectrumHeight = SPECTRUM_HEIGHT;
		
		settings.calibrationProfile = this.getFitting().getCalibrationProfile();
		settings.selectedPoints = this.getSelection().getPoints();
			
		
		
		switch (settings.mode) {
		case COMPOSITE:
			settings.spectrumTitle = "Intensity (counts)";
			break;
		case OVERLAY:
			settings.spectrumTitle = "Colour" +
					(this.getFitting().getMapScaleMode() == MapScaleMode.RELATIVE ? " - Colours scaled independently" : "");
			break;
		case RATIO:
			settings.spectrumTitle = "Intensity (ratio)" + (this.getFitting().getMapScaleMode() == MapScaleMode.RELATIVE ? " - sides scaled independently" : "");
			break;
		}
		
		String filterActions = filteringController.getActionDescription();
		if (filterActions != null) {
			settings.spectrumTitle += " - " + filterActions;
		}
		
		return settings;
		
	}
	
	public MapRenderData getMapRenderData() {
		
		MapRenderData data = new MapRenderData();
		
		switch (getFitting().getMapDisplayMode()) {
		case COMPOSITE:
			data.compositeData = this.getFitting().getCompositeMapData();
			break;
		case OVERLAY:
			data.overlayData = this.getFitting().getOverlayMapData();
			break;
		case RATIO:
			data.ratioData = this.getFitting().getRatioMapData();
			break;
		}
		data.maxIntensity = this.getFitting().sumAllTransitionSeriesMaps().max();
		
		return data;
		
	}
	

}
