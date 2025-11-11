package org.peakaboo.ui.swing.mapping.components;

import javax.swing.*;
import javax.swing.border.MatteBorder;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.KeyValuePill;
import org.peakaboo.framework.stratus.components.ui.ZoomSlider;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonConfig;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonLayout;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonSize;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayout;
import org.peakaboo.ui.swing.app.widgets.StatusBarPillStrip;
import org.peakaboo.ui.swing.mapping.MapperPanel;

import java.awt.*;


public class MapStatusBar extends JPanel {

	private MappingController controller;
	private MapperPanel parent;
	private StatusBarPillStrip pills;
	private KeyValuePill pIndex, pX, pY, pValue;
	
	public MapStatusBar(MappingController controller, MapperPanel parent) {
		
		this.controller = controller;
		this.parent = parent;
		
		
		pills = new StatusBarPillStrip();
		this.add(pills);
		
		int indexdigits = (int)Math.ceil(Math.log10(controller.rawDataController.getMapSize()));
		pIndex = new KeyValuePill("Index", indexdigits);
		pX = new KeyValuePill("X", Math.max(indexdigits, 4));
		pY = new KeyValuePill("Y", Math.max(indexdigits, 4));
		pValue = new KeyValuePill("Value", 10);
		
		pills.addPills(pIndex, pX, pY, pValue);
		
		//status text
		
		showValueAtCoord(null);
		
		
		
		
		
		//zoom controls
		ZoomSlider zoom = new ZoomSlider(10, 100, 1, value -> controller.getSettings().setZoom(value / 10f));
		zoom.setOpaque(false);
		zoom.setBorder(Spacing.bMedium());
		
		JPopupMenu zoomMenu = new JPopupMenu();
		zoomMenu.setBorder(Spacing.bNone());
		zoomMenu.add(zoom);
		FluentButton zoomButton = new FluentButton().withIcon(StockIcon.FIND, Stratus.getTheme().getControlText())
				.withTooltip("Zoom")
				.withLayout(FluentButtonLayout.IMAGE)
				.withBordered(false);
		zoomButton.withAction(() -> {
			int x = (int)((-zoomMenu.getPreferredSize().getWidth() + zoomButton.getSize().getWidth()) / 2f);
			int y = (int)-zoomMenu.getPreferredSize().getHeight();
			zoomMenu.show(zoomButton, x, y);
		});
		
		// Copy button
		Timer[] timer = new Timer[1];
		FluentButton copy = new FluentButton()
				.withIcon(StockIcon.EDIT_COPY)
				.withButtonSize(FluentButtonSize.COMPACT)
				.withBordered(FluentButtonConfig.BorderStyle.ACTIVE)
				.withTooltip("Copy the current map as CSV")
				.withAction(() -> {
					// Do the copy
					parent.copyInteraction(controller.getCSV(), timer);
				});
		
		
		ClearPanel controls = new ClearPanel(new FlowLayout(SwingConstants.EAST, 0, 0));
		controls.setBorder(Spacing.bNone());
		controls.add(copy);
		controls.add(zoomButton);
		this.add(controls);

		this.setBorder(new MatteBorder(1, 0, 0, 0, Stratus.getTheme().getWidgetBorder()));
		this.setLayout(new HeaderLayout(null, pills, controls));
		
		
	}
		
	public void showValueAtCoord(Coord<Integer> mapCoord) {
		var info = controller.getFitting().getInfoAtPoint(mapCoord);
		if (info.isPresent()) {
			var index = info.get().index();
			if (index == -1) {
				pIndex.setVisible(false);
			} else {
				pIndex.setVisible(true);
				pIndex.setValue(index);	
			}
			
			pX.setValue(info.get().x());
			pY.setValue(info.get().y());
			pValue.setValue(info.get().value());
		} else {
			pIndex.setValue("");
			pX.setValue("");
			pY.setValue("");
			pValue.setValue("");
		}
		
	}
	
}
