package peakaboo.controller.mapper;



import java.util.List;

import eventful.EventfulType;
import peakaboo.controller.mapper.data.MapSetController;
import peakaboo.controller.mapper.settings.MapSettingsController;
import peakaboo.controller.mapper.settings.MapViewSettings;
import peakaboo.controller.plotter.PlotController;
import peakaboo.controller.settings.SavedSettings;
import peakaboo.datasource.model.internal.CroppedDataSource;
import peakaboo.datasource.model.internal.SelectionDataSource;
import scitypes.Coord;


public class MappingController extends EventfulType<String>
{

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
	
	
	public SavedSettings getSavedSettings() {
		return plotcontroller.getSavedSettings();
	}
	
	

}
