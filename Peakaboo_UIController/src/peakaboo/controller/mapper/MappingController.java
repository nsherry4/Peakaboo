package peakaboo.controller.mapper;



import java.io.InputStream;

import peakaboo.controller.mapper.mapset.MapSetController;
import peakaboo.controller.mapper.maptab.MapTabController;
import peakaboo.controller.plotter.IPlotController;
import peakaboo.datasource.DataSource;
import scitypes.Coord;
import eventful.EventfulType;
import eventful.EventfulTypeListener;


public class MappingController extends EventfulType<String>
{

	public enum UpdateType
	{
		DATA_OPTIONS, DATA, UI_OPTIONS, BOUNDING_REGION, TABS;
	}
	
	
	public 	MapSetController		mapsController;
	private MapTabController		tabController;
	
	private EventfulTypeListener<String> controllerListener;
	
	private IPlotController		plotcontroller;
	
	
	public MappingController(IPlotController plotcontroller)
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
	public MappingController(MappingController copy, IPlotController plotcontroller)
	{
		mapsController = new MapSetController(copy.mapsController);
		
		initialize(plotcontroller);
		
	}
	
	
	private void initialize(IPlotController plotcontroller)
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
		return plotcontroller.data().getDataSourceForSubset(mapsController.getDataWidth(), mapsController.getDataHeight(), cstart, cend);
	}
	
	public InputStream getSerializedPlotSettings()
	{
		return plotcontroller.getSerializedPlotSettings();
	}
	

}
