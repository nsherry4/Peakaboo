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
		return plotPanel;
		
	}
	
	public PlotPanel newTab()
	{
		return plotterFrame.getTabControl().newTab();
	}
	
	
	

}
