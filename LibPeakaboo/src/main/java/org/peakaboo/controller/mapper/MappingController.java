package org.peakaboo.controller.mapper;



import java.util.List;

import org.peakaboo.controller.mapper.dimensions.MapDimensionsController;
import org.peakaboo.controller.mapper.filtering.MapFilteringController;
import org.peakaboo.controller.mapper.rawdata.RawDataController;
import org.peakaboo.controller.mapper.selection.AreaSelection;
import org.peakaboo.controller.mapper.selection.MapSelectionController;
import org.peakaboo.controller.mapper.selection.PointsSelection;
import org.peakaboo.controller.mapper.settings.MapSettingsController;
import org.peakaboo.controller.mapper.settings.MapViewSettings;
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
	private MapSettingsController	display;
	private MapFilteringController 	filteringController;
	private MapSelectionController	selectionController;
	private MapDimensionsController dimensionsController;
	
	private PlotController			plotcontroller;
	
	
	/**
	 * This constructor copies the user preferences from the map,
	 * and directly references the map data
	 * @param copy
	 * @param plotcontroller
	 */
	public MappingController(RawDataController rawDataController, MapViewSettings copyViewSettings, MapDimensionsController copyUserDimensions, PlotController plotcontroller)
	{
		this.plotcontroller = plotcontroller;
		
		this.rawDataController = rawDataController;
		rawDataController.addListener(this::updateListeners);
		
		this.filteringController = new MapFilteringController(this);
		filteringController.addListener(this::updateListeners);
		
		this.selectionController = new MapSelectionController(this);
		selectionController.addListener(this::updateListeners);
		
		this.display = new MapSettingsController(this, copyViewSettings);		
		display.addListener(this::updateListeners);		
		
		this.dimensionsController = new MapDimensionsController(this, copyUserDimensions);	
		dimensionsController.addListener(this::updateListeners);
		
	}
	

	public MapSettingsController getSettings() {
		return display;
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
		
		settings.showDatasetTitle = this.display.getView().getShowDatasetTitle();
		settings.datasetTitle = this.rawDataController.getDatasetTitle();
		settings.showScaleBar = this.display.getView().getShowScaleBar();
		settings.showMapTitle = this.display.getView().getShowTitle();
		settings.mapTitle = this.getSettings().getMapFittings().mapLongTitle();
		
		settings.logTransform = this.getSettings().getMapFittings().isLogView();
		settings.scalemode = this.getSettings().getMapFittings().getMapScaleMode();
		settings.monochrome = this.display.getView().getMonochrome();
		settings.contours = this.display.getView().getContours();
		settings.contourSteps = this.display.getView().getSpectrumSteps();
		settings.overlayLowCutoff = this.display.getView().getOverlayLowCutoff();
		
		settings.mode = this.getSettings().getMapFittings().getMapDisplayMode();
				
		settings.drawCoord = this.display.getView().getDrawCoords();
		settings.coordLoXLoY = this.getSettings().getView().getLoXLoYCoord();
		settings.coordHiXLoY = this.getSettings().getView().getHiXLoYCoord();
		settings.coordLoXHiY = this.getSettings().getView().getLoXHiYCoord();
		settings.coordHiXHiY = this.getSettings().getView().getHiXHiYCoord();
		settings.physicalUnits = this.rawDataController.getRealDimensionUnits();
		settings.physicalCoord = this.rawDataController.getRealDimensions() != null;
		
		settings.showSpectrum = this.display.getView().getShowSpectrum();
		settings.spectrumHeight = SPECTRUM_HEIGHT;
		
		settings.calibrationProfile = this.getSettings().getMapFittings().getCalibrationProfile();
		settings.selectedPoints = this.getSelection().getPoints();
			
		
		
		switch (settings.mode) {
		case COMPOSITE:
			settings.spectrumTitle = this.getSettings().getMapFittings().isLogView() ? "Log Scale Intensity (counts)" : "Intensity (counts)";
			break;
		case OVERLAY:
			settings.spectrumTitle = 
					(this.getSettings().getView().getOverlayLowCutoff() == 0f ? "Colour" : "High Intensities") +
					(this.getSettings().getMapFittings().isLogView() ? " (Log Scale)" : "") + 
					(this.getSettings().getMapFittings().getMapScaleMode() == MapScaleMode.RELATIVE ? " - Colours scaled independently" : "");
			break;
		case RATIO:
			settings.spectrumTitle = "Intensity (ratio)" + (this.getSettings().getMapFittings().getMapScaleMode() == MapScaleMode.RELATIVE ? " - sides scaled independently" : "");
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
		
		switch (getSettings().getMapFittings().getMapDisplayMode()) {
		case COMPOSITE:
			data.compositeData = this.getSettings().getMapFittings().getCompositeMapData();
			break;
		case OVERLAY:
			data.overlayData = this.getSettings().getMapFittings().getOverlayMapData();
			break;
		case RATIO:
			data.ratioData = this.getSettings().getMapFittings().getRatioMapData();
			break;
		}
		data.maxIntensity = this.getSettings().getMapFittings().sumAllTransitionSeriesMaps().max();
		
		return data;
		
	}
	

}
