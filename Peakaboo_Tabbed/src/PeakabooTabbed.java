import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import peakaboo.common.Version;
import peakaboo.curvefit.peaktable.PeakTableReader;
import peakaboo.ui.swing.container.TabbedContainer;
import peakaboo.ui.swing.plotting.PlotPanel;


import swidget.Swidget;
import swidget.icons.IconFactory;
import swidget.icons.StockIcon;
import swidget.widgets.ToolbarImageButton;
import swidget.widgets.tabbedinterface.TabbedInterface;


public class PeakabooTabbed extends JFrame
{

	
	TabbedInterface<PlotPanel> tabs;
	private Map<PlotPanel, TabbedContainer> containers;
	
	public PeakabooTabbed()
	{
	
		containers = new HashMap<PlotPanel, TabbedContainer>();
		
		tabs = new TabbedInterface<PlotPanel>("No Data") {

			@Override
			protected PlotPanel createComponent()
			{
				TabbedContainer container = new TabbedContainer(PeakabooTabbed.this, tabs);
				PlotPanel plot =  new PlotPanel(container);
				plot.setProgramTitle("");
				container.setPlotPanel(plot);
				containers.put(plot, container);
				
				ToolbarImageButton newTab = new ToolbarImageButton(StockIcon.WINDOW_TAB_NEW, "New Tab");
				newTab.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						newTab();
					}
				});
				plot.addToolbarButton(1, newTab);
				
				return plot;
			}

			@Override
			protected void destroyComponent(PlotPanel component){}

			@Override
			protected void tabsChanged(String title)
			{
				TabbedContainer container = containers.get(getActiveTab());
				setJMenuBar(container.getJMenuBar());
				String windowTitle = "";
				windowTitle += title + " - " + Version.title;
				setTitle(windowTitle);
			}};
			
		
		
		setPreferredSize(new Dimension(1000, 473));
		setTitle("Skew");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tabs.newTab();
		
		add(tabs);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		
	}

	
	public void newTab()
	{
		tabs.newTab();
	}
	
	public void setTabTitle(PlotPanel component, String title)
	{
		tabs.setTabTitle(component, title);
	}
	
	public static void main(String[] args)
	{
		System.setProperty("sun.java2d.pmoffscreen", "false");
		PeakTableReader.readPeakTable();
		
		Swidget.initialize();
		IconFactory.customPath = "/peakaboo/ui/swing/icons/";

		new PeakabooTabbed();
	}
	
}
