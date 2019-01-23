package org.peakaboo.ui.swing.mapping;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import org.peakaboo.common.Version;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.rawdata.RawDataController;
import org.peakaboo.controller.mapper.settings.MapViewSettings;
import org.peakaboo.controller.plotter.PlotController;

import swidget.icons.IconFactory;
import swidget.widgets.LiveFrame;
import swidget.widgets.tabbedinterface.TabbedInterface;
import swidget.widgets.tabbedinterface.TabbedLayerPanel;


/**
 * 
 * This class is the mapping window for Peakaboo
 * 
 * @author Nathaniel Sherry, 2009
 */

public class MapperFrame extends LiveFrame
{

	private TabbedInterface<TabbedLayerPanel> tabs;
	private PlotController plotController;

	private TabbedInterface<TabbedLayerPanel> parentPlotter;
	private MapViewSettings previousMapSettings;
	private RawDataController mapData;
	
	
	public MapperFrame(TabbedInterface<TabbedLayerPanel> plotter, RawDataController mapData, MapViewSettings previousMapSettings, PlotController plotcontroller)
	{
		super();
			
		this.plotController = plotcontroller;
		this.parentPlotter = plotter;
		this.previousMapSettings = previousMapSettings;
		this.mapData = mapData;
		
		init();

		setLocationRelativeTo(plotter.getWindow());
				
	}
	
	
	
	
	
	public TabbedInterface<TabbedLayerPanel> getParentPlotter() {
		return parentPlotter;
	}



	private void init()
	{
		setPreferredSize(new Dimension(900, 700));


		
		Container pane = this.getContentPane();
		pane.setLayout(new BorderLayout());
		
		tabs = new TabbedInterface<TabbedLayerPanel>(this, TabbedLayerPanel::getTabTitle) {

			@Override
			protected MapperPanel createComponent() {
				return createMapperPanel();
			}

			@Override
			protected void destroyComponent(TabbedLayerPanel component) {}

			@Override
			protected void titleChanged(String title) {
				//setTitle(title);
			}
		};
		setTitle("Peakaboo");
		setIconImage(IconFactory.getImage(Version.icon));
		
		pane.add(tabs, BorderLayout.CENTER);
		this.pack();
		
		tabs.init();


	}

	private MapperPanel createMapperPanel()
	{
		MapViewSettings lastMapSettings = previousMapSettings;
		if (tabs.getActiveTab() != null) {
			TabbedLayerPanel lastTab = tabs.getActiveTab();
			if (lastTab instanceof MapperPanel) {
				lastMapSettings = ((MapperPanel)lastTab).controller.getSettings().getView();
			}
		}
		
		MappingController newController = new MappingController(mapData, lastMapSettings, plotController);
		final MapperPanel viewer = new MapperPanel(newController, parentPlotter, tabs);
		return viewer;					
	}

	

	
	
}
