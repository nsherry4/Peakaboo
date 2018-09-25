package peakaboo.controller.mapper;



import java.util.List;

import eventful.EventfulType;
import peakaboo.controller.mapper.data.MapRenderData;
import peakaboo.controller.mapper.data.MapSetController;
import peakaboo.controller.mapper.settings.AreaSelection;
import peakaboo.controller.mapper.settings.MapRenderSettings;
import peakaboo.controller.mapper.settings.MapScaleMode;
import peakaboo.controller.mapper.settings.MapSettingsController;
import peakaboo.controller.mapper.settings.MapViewSettings;
import peakaboo.controller.mapper.settings.PointsSelection;
import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.settings.SavedSession;
import peakaboo.datasource.model.internal.CroppedDataSource;
import peakaboo.datasource.model.internal.SelectionDataSource;
import scitypes.Coord;


public class MappingController extends EventfulType<String>
{
	
	private static final int SPECTRUM_HEIGHT = 15;

	public enum UpdateType
	{
		DATA_OPTIONS, DATA, UI_OPTIONS, AREA_SELECTION, POINT_SELECTION;
	}
	
	
	public 	MapSetController		mapsController;
	private MapSettingsController	display;

	private PlotController			plotcontroller;
	
	
	/**
	 * This constructor copies the user preferences from the map,
	 * and directly references the map data
	 * @param copy
	 * @param plotcontroller
	 */
	public MappingController(MapSetController data, MapViewSettings copyViewSettings, PlotController plotcontroller)
	{
		this.mapsController = data;
		initialize(plotcontroller, copyViewSettings);
		
	}
	
	
	private void initialize(PlotController plotcontroller, MapViewSettings copyViewSettings)
	{
		display = new MapSettingsController(this, copyViewSettings);

		mapsController.addListener(this::updateListeners);
		display.addListener(this::updateListeners);		

		this.plotcontroller = plotcontroller;
	}
	

	

	public MapSettingsController getSettings() {
		return display;
	}

	public CroppedDataSource getDataSourceForSubset(Coord<Integer> cstart, Coord<Integer> cend)
	{
		return plotcontroller.data().getDataSourceForSubset(getSettings().getView().getDataWidth(), getSettings().getView().getDataHeight(), cstart, cend);
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
		settings.dataWidth = this.display.getView().getDataWidth(); 
		settings.dataHeight = this.display.getView().getDataHeight();
		settings.interpolatedWidth = this.display.getView().getInterpolatedWidth();
		settings.interpolatedHeight = this.display.getView().getInterpolatedHeight();
		
		settings.showDatasetTitle = this.display.getView().getShowDatasetTitle();
		settings.datasetTitle = this.mapsController.getDatasetTitle();
		
		settings.showMapTitle = this.display.getView().getShowTitle();
		settings.mapTitle = this.getSettings().getMapFittings().mapLongTitle();
		
		settings.logTransform = this.getSettings().getMapFittings().isLogView();
		settings.scalemode = this.getSettings().getMapFittings().getMapScaleMode();
		settings.monochrome = this.display.getView().getMonochrome();
		settings.contours = this.display.getView().getContours();
		settings.contourSteps = this.display.getView().getSpectrumSteps();
			
		settings.mode = this.getSettings().getMapFittings().getMapDisplayMode();
		
		
		settings.drawCoord = this.display.getView().getDrawCoords();
		settings.coordTL = this.getSettings().getView().getTopLeftCoord();
		settings.coordTR = this.getSettings().getView().getTopRightCoord();
		settings.coordBL = this.getSettings().getView().getBottomLeftCoord();
		settings.coordBR = this.getSettings().getView().getBottomRightCoord();
		settings.physicalUnits = this.mapsController.getRealDimensionUnits();
		settings.physicalCoord = this.mapsController.getRealDimensions() != null;
		
		settings.showSpectrum = this.display.getView().getShowSpectrum();
		settings.spectrumHeight = SPECTRUM_HEIGHT;
		

		
		
		//There should only ever be one selection active at a time
		AreaSelection areaSelection = this.getSettings().getAreaSelection();
		if (areaSelection.hasSelection()) {
			settings.selectedPoints = areaSelection.getPoints();
		}
		
		PointsSelection pointsSelection = this.getSettings().getPointsSelection();
		if (pointsSelection.hasSelection()) {
			settings.selectedPoints = pointsSelection.getPoints();
		}
		
		
		switch (settings.mode) {
		case COMPOSITE:
			settings.spectrumTitle = this.getSettings().getMapFittings().isLogView() ? "Log Scale Intensity (counts)" : "Intensity (counts)";
			break;
		case OVERLAY:
			settings.spectrumTitle = "Colour" +
					(this.getSettings().getMapFittings().isLogView() ? " (Log Scale)" : "") + 
					(this.getSettings().getMapFittings().getMapScaleMode() == MapScaleMode.RELATIVE ? " - Colours scaled independently" : "");
			break;
		case RATIO:
			settings.spectrumTitle = "Intensity (ratio)" + (this.getSettings().getMapFittings().getMapScaleMode() == MapScaleMode.RELATIVE ? " - Ratio sides scaled independently" : "");
			break;
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
