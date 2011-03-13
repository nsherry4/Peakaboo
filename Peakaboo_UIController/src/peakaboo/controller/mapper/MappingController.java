package peakaboo.controller.mapper;



import java.io.InputStream;

import eventful.EventfulListener;
import eventful.EventfulType;
import eventful.EventfulTypeListener;

import peakaboo.controller.mapper.mapset.MapSetController;
import peakaboo.controller.mapper.maptab.MapTabController;
import peakaboo.controller.plotter.PlotController;
import peakaboo.fileio.DataSource;

import scitypes.Coord;


public class MappingController extends EventfulType<String>
{

	public enum UpdateType
	{
		DATA_OPTIONS, DATA, UI_OPTIONS, BOUNDING_REGION, TABS;
	}
	
	
	public 	MapSetController		mapsController;
	private MapTabController		tabController;
	
	private EventfulTypeListener<String> controllerListener;
	
	private PlotController		plotcontroller;
	
	
	public MappingController(PlotController plotcontroller)
	{
		
		mapsController = new MapSetController();

		initialize(plotcontroller);
		
	}

	/**
	 * This copy constructor copies only the user preferences from the map,
	 * and does not copy anthing else like map data
	 * @param copy
	 * @param plotcontroller
	 */
	public MappingController(MappingController copy, PlotController plotcontroller)
	{
		mapsController = new MapSetController(copy.mapsController);
		
		initialize(plotcontroller);
		
	}
	
	
	private void initialize(PlotController plotcontroller)
	{
		tabController = null;
		
		controllerListener = new EventfulTypeListener<String>() {

			public void change(String message)
			{
				updateListeners(message);
			}};
		
		mapsController.addListener(controllerListener);
		

		this.plotcontroller = plotcontroller;
	}
	

	public void setTabController(MapTabController s)
	{
		if (tabController != null)
		{
			tabController.removeListener(controllerListener);
		}
		
		tabController = s;
		
		s.addListener(controllerListener);
	}
	
	public MapTabController getActiveTabController()
	{
		return tabController;
	}
	
	


	public DataSource getDataSourceForSubset(Coord<Integer> cstart, Coord<Integer> cend)
	{
		return plotcontroller.dataController.getDataSourceForSubset(mapsController.getDataWidth(), mapsController.getDataHeight(), cstart, cend);
	}
	
	public InputStream getSerializedPlotSettings()
	{
		return plotcontroller.getSerializedPlotSettings();
	}
	

}
