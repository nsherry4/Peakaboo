package peakaboo.ui.swing.mapping;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JFrame;

import eventful.EventfulTypeListener;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.mapset.MapSetController;
import peakaboo.controller.mapper.mapview.MapSettings;
import peakaboo.controller.plotter.PlotController;
import peakaboo.ui.swing.plotting.tabbed.TabbedPlotterManager;
import swidget.widgets.tabbedinterface.TabbedInterface;


/**
 * 
 * This class is the mapping window for Peakaboo
 * 
 * @author Nathaniel Sherry, 2009
 */

public class MapperFrame extends JFrame
{

	private TabbedInterface<MapperPanel> tabs;

	protected PlotController	plotController;
		
	EventfulTypeListener<String> controllerListener;
	
	private TabbedPlotterManager 	parentPlotter;
	private MapSettings 			previousMapSettings;
	private MapSetController 		mapData;
	
	
	public MapperFrame(TabbedPlotterManager plotter, MapSetController mapData, MapSettings previousMapSettings, PlotController plotcontroller)
	{
		super();
		this.plotController = plotcontroller;
		this.parentPlotter = plotter;
		this.previousMapSettings = previousMapSettings;
		this.mapData = mapData;
		
		init();

		setLocationRelativeTo(plotter.getWindow());
		
	}
	
	
	
	
	
	public TabbedPlotterManager getParentPlotter() {
		return parentPlotter;
	}



	public MapSettings showDialog()
	{
		setVisible(true);
		
		return tabs.getActiveTab().controller.settings;
	}


	private void init()
	{
		setPreferredSize(new Dimension(900, 700));


		
		Container pane = this.getContentPane();
		pane.setLayout(new BorderLayout());
		
		tabs = new TabbedInterface<MapperPanel>(MapperPanel::getTitle) {

			@Override
			protected MapperPanel createComponent() {
				return createMapperPanel();
			}

			@Override
			protected void destroyComponent(MapperPanel component) {}

			@Override
			protected void titleChanged(String title) {
				setTitle(title);
			}
		};
		

		pane.add(tabs, BorderLayout.CENTER);
		this.pack();
		
		tabs.init();


	}

	private MapperPanel createMapperPanel()
	{
		MapSettings lastMapSettings = previousMapSettings;
		if (tabs.getActiveTab() != null) {
			lastMapSettings = tabs.getActiveTab().controller.settings;
		}
		
		MappingController newController = new MappingController(mapData, lastMapSettings, plotController);
		final MapperPanel viewer = new MapperPanel(newController, parentPlotter, tabs, this);
		return viewer;					
	}

	

	
	
}
