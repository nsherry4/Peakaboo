package org.peakaboo.ui.swing.mapping;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collections;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.SavedMapSession;
import org.peakaboo.controller.mapper.rawdata.RawDataController;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.mapping.rawmap.RawMapSet;
import org.peakaboo.ui.swing.mapping.sidebar.MapDimensionsPanel;

import cyclops.util.Mutable;
import swidget.icons.StockIcon;
import swidget.widgets.buttons.ToolbarImageButton;
import swidget.widgets.layerpanel.HeaderLayer;
import swidget.widgets.layerpanel.LayerPanel;

public class QuickMapPanel extends HeaderLayer {

	private MappingController controller;
	private MapCanvas canvas;
	
	public QuickMapPanel(LayerPanel owner, int channel, RawMapSet maps, Mutable<SavedMapSession> previousMapSession, PlotController plotcontroller) {
		super(owner, true, true);
		
		RawDataController rawDataController = new RawDataController();
		rawDataController.setMapData(maps, "", Collections.emptyList(), null, null, null, new CalibrationProfile());
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
		
		
		getHeader().setCentre("QuickMap of Channel " + channel);
		getHeader().setRight(MapperToolbar.createOptionsButton(owner, controller));
		getHeader().setLeft(createDimensionsButton(owner, controller));
				
	}
	
	public static ToolbarImageButton createDimensionsButton(LayerPanel panel, MappingController controller) {
		
		ToolbarImageButton opts = new ToolbarImageButton();
		opts.withIcon(StockIcon.MISC_PROPERTIES).withTooltip("Map Dimensions Menu");
		JPopupMenu menu = new JPopupMenu();
		
		MapDimensionsPanel dimensions = new MapDimensionsPanel(panel, controller, true);
		dimensions.getGuessDimensionsButton().addActionListener(e -> {
			menu.setVisible(false);
		});
		dimensions.setOpaque(false);
		menu.add(dimensions);
		
		opts.addActionListener(e -> menu.show(opts, 0, opts.getHeight()));
		
		return opts;
	}
	
}
