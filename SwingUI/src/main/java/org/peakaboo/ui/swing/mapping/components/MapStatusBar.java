package org.peakaboo.ui.swing.mapping.components;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.MatteBorder;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.KeyValuePill;
import org.peakaboo.framework.stratus.components.ui.ZoomSlider;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonLayout;
import org.peakaboo.framework.stratus.components.ui.header.HeaderLayout;
import org.peakaboo.ui.swing.app.widgets.StatusBarPillStrip;

public class MapStatusBar extends JPanel {

	private MappingController controller;
	private StatusBarPillStrip pills;
	private KeyValuePill pIndex, pX, pY, pValue;
	
	public MapStatusBar(MappingController controller) {
		
		this.controller = controller;
		
		
		
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
		
		this.add(zoomButton);

		this.setBorder(new MatteBorder(1, 0, 0, 0, Stratus.getTheme().getWidgetBorder()));
		this.setLayout(new HeaderLayout(null, pills, zoomButton));
		
		
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
