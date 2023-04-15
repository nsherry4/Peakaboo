package org.peakaboo.ui.swing.mapping;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.peakaboo.app.Version;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.SavedMapSession;
import org.peakaboo.controller.mapper.rawdata.RawDataController;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.framework.cyclops.util.Mutable;
import org.peakaboo.framework.stratus.api.icons.IconFactory;
import org.peakaboo.framework.stratus.components.ui.live.LiveFrame;
import org.peakaboo.framework.stratus.components.ui.tabui.TabbedInterface;
import org.peakaboo.framework.stratus.components.ui.tabui.TabbedLayerPanel;
import org.peakaboo.tier.Tier;


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
	private Mutable<SavedMapSession> previousMapSession;
	private TabbedInterface<TabbedLayerPanel> parentPlotter;
	private RawDataController mapData;
	
	
	public MapperFrame(
			TabbedInterface<TabbedLayerPanel> plotter, 
			RawDataController mapData, 
			Mutable<SavedMapSession> lastMapSession,
			PlotController plotcontroller
		)
	{
		super();
			
		this.plotController = plotcontroller;
		this.parentPlotter = plotter;
		this.previousMapSession = lastMapSession;
		this.mapData = mapData;
		
		initFrame();

		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				MapperPanel mapPanel = (MapperPanel) tabs.getActiveTab();
				previousMapSession.set(new SavedMapSession().storeFrom(mapPanel.controller));
			}

		});
		
		setLocationRelativeTo(plotter.getWindow());
				
	}
	
	
	
	
	
	public TabbedInterface<TabbedLayerPanel> getParentPlotter() {
		return parentPlotter;
	}



	private void initFrame() {
		setPreferredSize(new Dimension(900, 700));


		
		Container pane = this.getContentPane();
		pane.setLayout(new BorderLayout());
		
		tabs = new TabbedInterface<TabbedLayerPanel>(this, TabbedLayerPanel::getTabTitle) {

			@Override
			protected MapperPanel createComponent() {
				return createMapperPanel();
			}

			@Override
			protected void destroyComponent(TabbedLayerPanel component) {
				// NOOP
			}

			@Override
			protected void titleChanged(String title) {
				// NOOP
			}

			@Override
			protected void titleDoubleClicked(TabbedLayerPanel component) {
				// NOOP
			}
		};
		setTitle("Peakaboo");
		setIconImage(IconFactory.getImage(Tier.provider().iconPath(), Version.logo));
		
		pane.add(tabs, BorderLayout.CENTER);
		this.pack();
		
		tabs.init();


	}

	private MapperPanel createMapperPanel() {
		SavedMapSession mapSession = previousMapSession.get();
		if (tabs.getActiveTab() != null) {
			TabbedLayerPanel lastTab = tabs.getActiveTab();
			if (lastTab instanceof MapperPanel mp) {
				mapSession = new SavedMapSession().storeFrom(mp.controller);
			}
		}
		
		MappingController newController = new MappingController(mapData, plotController);
		if (mapSession != null) {
			mapSession.loadInto(newController);
		}
		
		return new MapperPanel(newController, parentPlotter, tabs);				
	}

}
