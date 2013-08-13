package peakaboo.ui.swing.container;

import java.awt.Container;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import peakaboo.ui.swing.plotting.PlotPanel;

import swidget.widgets.tabbedinterface.TabbedInterface;

public class TabbedContainer implements PeakabooContainer
{
	
	private JFrame window;
	private TabbedInterface<PlotPanel> tabs;
	private PlotPanel plotpanel;
	private String title;
	private JMenuBar menubar;
	
	public TabbedContainer(JFrame window, TabbedInterface<PlotPanel> tabs)
	{
		this.window = window;
		this.tabs = tabs;
	}
	
	public void setPlotPanel(PlotPanel plotpanel)
	{
		this.plotpanel = plotpanel;
	}
	
	@Override
	public Container getContainer()
	{
		return window;
	}

	@Override
	public Window getWindow()
	{
		return window;
	}

	@Override
	public void setTitle(String title)
	{
		if (title.trim().length() == 0) title = "No Data";
		tabs.setTabTitle(plotpanel, title);
		this.title = title;
	}

	@Override
	public void setJMenuBar(JMenuBar menubar)
	{
		this.menubar = menubar;
	}
	
	public JMenuBar getJMenuBar()
	{
		return menubar;
	}

}
