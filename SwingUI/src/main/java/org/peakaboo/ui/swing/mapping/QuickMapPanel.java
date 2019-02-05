package org.peakaboo.ui.swing.mapping;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collections;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.rawdata.RawDataController;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.mapping.rawmap.RawMapSet;
import org.peakaboo.ui.swing.mapping.sidebar.MapDimensionsPanel;

import swidget.icons.StockIcon;
import swidget.widgets.buttons.ToolbarImageButton;
import swidget.widgets.layerpanel.HeaderLayer;
import swidget.widgets.layerpanel.LayerPanel;

public class QuickMapPanel extends HeaderLayer {

	private MappingController controller;
	private MapCanvas canvas;
	
	public QuickMapPanel(LayerPanel owner, int channel, RawMapSet maps, PlotController plotcontroller) {
		super(owner, true);
		
		RawDataController rawDataController = new RawDataController();
		rawDataController.setMapData(maps, "", Collections.emptyList(), null, null, null, new CalibrationProfile());
		this.controller = new MappingController(rawDataController, plotcontroller);
		
//		controller.getUserDimensions().guessDataDimensions().run().ifPresent(coord -> {
//			controller.getUserDimensions().setUserDataWidth(coord.x);
//			controller.getUserDimensions().setUserDataHeight(coord.y);
//			System.out.println(coord);
//		});
		
		controller.getSettings().setShowCoords(true);
		controller.getSettings().setShowDatasetTitle(false);
		controller.getSettings().setShowScaleBar(false);
		controller.getSettings().setShowSpectrum(false);
		controller.getSettings().setShowTitle(false);
		
		
		
		
		
		JPanel body = new JPanel(new BorderLayout());
		canvas = new MapCanvas(controller, false);
		canvas.setPreferredSize(new Dimension(500, 300));
		body.add(canvas, BorderLayout.CENTER);
		
//		JPanel sidebar = new JPanel(new BorderLayout());
//		MapDimensionsPanel dimensions = new MapDimensionsPanel(owner, controller, true);
//		sidebar.add(dimensions, BorderLayout.NORTH);
//		body.add(sidebar, BorderLayout.WEST);
		
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
		dimensions.setOpaque(false);
		menu.add(dimensions);
		
		opts.addActionListener(e -> menu.show(opts, 0, opts.getHeight()));
		
		return opts;
	}
	
}
