package org.peakaboo.ui.swing.mapping;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collections;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.SavedMapSession;
import org.peakaboo.controller.mapper.rawdata.RawDataController;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.dataset.DataSet;
import org.peakaboo.framework.cyclops.util.Mutable;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ComponentStrip;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton.NotificationDotState;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentToolbarButton;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayer;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.framework.stratus.components.ui.tabui.TabbedInterface;
import org.peakaboo.framework.stratus.components.ui.tabui.TabbedLayerPanel;
import org.peakaboo.mapping.rawmap.RawMapSet;
import org.peakaboo.tier.Tier;
import org.peakaboo.ui.swing.app.PeakabooIcons;
import org.peakaboo.ui.swing.mapping.components.MapSelectionListener;
import org.peakaboo.ui.swing.mapping.components.MapperToolbar;
import org.peakaboo.ui.swing.mapping.components.PlotSelectionButton;
import org.peakaboo.ui.swing.mapping.sidebar.MapDimensionsPanel;
import org.peakaboo.ui.swing.mapping.sidebar.MapSelectionPanel.MapSelectionComponent;

public class QuickMapPanel extends HeaderLayer {

	private MappingController controller;
	private MapCanvas canvas;
	private FluentToolbarButton plotSelection, sizingButton, viewButton;
		
	public QuickMapPanel(
			LayerPanel plotTab, 
			TabbedInterface<TabbedLayerPanel> plotTabs, 
			int channel, 
			RawMapSet maps, 
			Mutable<SavedMapSession> previousMapSession, 
			PlotController plotcontroller
	) {
		super(plotTab, true, true);
		
		RawDataController rawDataController = new RawDataController();
		DataSet sourceDataset = plotcontroller.data().getDataSet();
		rawDataController.setMapData(maps, sourceDataset, "", Collections.emptyList(), Tier.provider().createDetectorProfile());
		this.controller = new MappingController(rawDataController, plotcontroller);
		
		// load saved dimensions, and when the window closes, save them
		// we don't save anything else because this is not the usual mapping window.
		// we don't want to load all the filters normally used in full mapping, for
		// example.
		SavedMapSession session = previousMapSession.get();
		if (session != null) {
			session.dimensions.loadInto(this.controller.getUserDimensions());
		}
		setOnClose(() -> {
			if (session != null) {
				session.dimensions.storeFrom(this.controller.getUserDimensions());
			} else {
				//if there is no session yet, we save the whole thing
				SavedMapSession newsession = new SavedMapSession().storeFrom(this.controller);
				previousMapSession.set(newsession);
			}
		});
		

		controller.getSettings().setShowCoords(true);
		controller.getSettings().setShowDatasetTitle(false);
		controller.getSettings().setShowScaleBar(false);
		controller.getSettings().setShowSpectrum(false);
		controller.getSettings().setShowTitle(false);
		

		JPanel body = new JPanel(new BorderLayout());
		canvas = new MapCanvas(controller, false);
		canvas.setPreferredSize(new Dimension(600, 300));
		body.add(canvas, BorderLayout.CENTER);
				
		setBody(body);
		
		
		viewButton = MapperToolbar.createOptionsButton(controller);
		viewButton.withIcon(PeakabooIcons.MENU_VIEW, IconSize.BUTTON, Stratus.getTheme().getControlText());
		viewButton.withButtonSize(FluentButtonSize.LARGE);
		sizingButton = createSizingButton(plotTab, controller);
		
		if (session == null ) {
			sizingButton.withNotificationDot(NotificationDotState.WARNING);
			sizingButton.withBordered(true);
		}

		//Create the selections widget and add a listener to keep it in sync w/ the model
		MapSelectionComponent selections = new MapSelectionComponent(selected -> controller.getSelection().setSelectionType(selected));
		controller.addListener(t -> {
			if (t == MapUpdateType.UI_OPTIONS) {
				selections.setSelection(controller.getSelection().getSelectionType());
			}
		});
		selections.setSelection(controller.getSelection().getSelectionType());
		
		plotSelection = new PlotSelectionButton(IconSize.BUTTON, controller, plotTabs);
			
				
		getHeader().setCentre("QuickMap of Channel " + channel);
		getHeader().setRight(new ComponentStrip(sizingButton, viewButton));
		getHeader().setLeft(new ComponentStrip(selections, plotSelection));
		
		MapSelectionListener selectionListener = new MapSelectionListener(canvas, controller);
		canvas.addMouseMotionListener(selectionListener);
		canvas.addMouseListener(selectionListener);
		
	}
	
	public FluentToolbarButton createSizingButton(LayerPanel panel, MappingController controller) {
		
		FluentToolbarButton opts = new FluentToolbarButton()
				.withIcon(StockIcon.OBJECT_FLIP_HORIZONTAL, IconSize.BUTTON, Stratus.getTheme().getControlText())
				.withTooltip("Map Dimensions Menu")
				.withButtonSize(FluentButtonSize.LARGE);
		JPopupMenu menu = new JPopupMenu();
		
		MapDimensionsPanel dimensions = new MapDimensionsPanel(panel, controller, true);
		dimensions.setBorder(Spacing.bMedium());
		dimensions.getMagicDimensionsButton().addActionListener(e -> menu.setVisible(false));
		dimensions.getResetDimensionsButton().ifPresent(reset -> reset.addActionListener(e -> menu.setVisible(false)));
		dimensions.setOpaque(false);
		menu.add(dimensions);
		
		opts.withAction(() -> {
			
			//Hide the notification dot, we got the user to take a look
			sizingButton.withNotificationDot(NotificationDotState.OFF);
			sizingButton.withBordered(false);
			
			int x = (int)(opts.getWidth() - menu.getPreferredSize().getWidth());
			int y = opts.getHeight();
			menu.show(opts, x, y);
		});
		
		return opts;
	}
	
}
