package peakaboo.ui.swing.plotting.tabbed;

import java.awt.Window;
import java.io.InputStream;

import peakaboo.datasource.model.DataSource;
import peakaboo.ui.swing.plotting.PlotPanel;


//simple controls for TabbedPlotterFrame
public class TabbedPlotterManager
{
	
	private TabbedPlotterFrame plotterFrame;
	
	public TabbedPlotterManager(TabbedPlotterFrame plotterFrame)
	{
		this.plotterFrame = plotterFrame;
	}

	public Window getWindow()
	{
		return plotterFrame;
	}

	public void setTitle(PlotPanel plotPanel, String title)
	{
		if (title.trim().length() == 0) title = "No Data";
		plotterFrame.getTabControl().setTabTitle(plotPanel, title);
	}
	
	public PlotPanel newTab(DataSource ds, String savedSettings)
	{

		PlotPanel plotPanel = newTab();
		
		//create a new datasource which is a subset of the passed one
		plotPanel.getController().data().setDataSource(ds);
		
		plotPanel.getController().loadSettings(savedSettings, false);
		
		//TODO: temporary work-around. Right now, the bad scan indexes aren't adjusted to fit the new data dimensions
		//so they cause good data to be discarded, or an index out of bounds exception to be thrown when the index
		//exceeds the dimensions of the new dataset
		plotPanel.getController().data().getDiscards().clear();		

		return plotPanel;
		
	}
	
	public PlotPanel newTab()
	{
		return plotterFrame.getTabControl().newTab();
	}
	
	
	

}
