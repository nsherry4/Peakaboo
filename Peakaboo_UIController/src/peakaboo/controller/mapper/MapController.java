package peakaboo.controller.mapper;



import java.io.InputStream;

import eventful.EventfulListener;
import eventful.EventfulType;
import eventful.EventfulTypeListener;

import peakaboo.controller.mapper.maps.AllMapsController;
import peakaboo.controller.mapper.maptab.ITabController;
import peakaboo.controller.mapper.maptab.TabController;
import peakaboo.controller.plotter.PlotController;
import peakaboo.fileio.DataSource;

import scitypes.Coord;


public class MapController extends EventfulType<String>
{

	public enum UpdateType
	{
		DATA_OPTIONS, DATA, UI_OPTIONS, BOUNDING_REGION, TABS;
	}
	
	
	public 	AllMapsController	mapsController;
	private TabController	tabController;
	
	private EventfulTypeListener<String> controllerListener;
	
	private PlotController		plotcontroller;
	
	
	public MapController(PlotController plotcontroller)
	{
		
		mapsController = new AllMapsController();
		tabController = null;
		
		controllerListener = new EventfulTypeListener<String>() {

			public void change(String message)
			{
				updateListeners(message);
			}};
		
		mapsController.addListener(controllerListener);
		

		this.plotcontroller = plotcontroller;
	}


	public void setTabController(TabController s)
	{
		if (tabController != null)
		{
			tabController.removeListener(controllerListener);
		}
		
		tabController = s;
		
		s.addListener(controllerListener);
	}
	
	public TabController getActiveTabController()
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
