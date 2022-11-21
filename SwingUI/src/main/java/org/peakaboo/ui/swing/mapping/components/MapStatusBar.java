package org.peakaboo.ui.swing.mapping.components;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.MatteBorder;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.ZoomSlider;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonLayout;

public class MapStatusBar extends JPanel {

	private JLabel status;
	private MappingController controller;
	
	public MapStatusBar(MappingController controller) {
		
		this.controller = controller;
		
		setLayout(new BorderLayout());
		
		
		//status text
		status = new JLabel("");
		status.setBorder(Spacing.bSmall());
		status.setHorizontalAlignment(JLabel.CENTER);
		status.setFont(status.getFont().deriveFont(Font.PLAIN));
		
		showValueAtCoord(null);
		
		add(status, BorderLayout.CENTER);
		
		
		
		//zoom controls
		ZoomSlider zoom = new ZoomSlider(10, 100, 1, value -> controller.getSettings().setZoom(value / 10f));
		zoom.setOpaque(false);
		zoom.setBorder(Spacing.bMedium());
		
		JPopupMenu zoomMenu = new JPopupMenu();
		zoomMenu.setBorder(Spacing.bNone());
		zoomMenu.add(zoom);
		FluentButton zoomButton = new FluentButton(StockIcon.FIND)
				.withTooltip("Zoom")
				.withLayout(FluentButtonLayout.IMAGE)
				.withBordered(false);
		zoomButton.withAction(() -> {
			int x = (int)((-zoomMenu.getPreferredSize().getWidth() + zoomButton.getSize().getWidth()) / 2f);
			int y = (int)-zoomMenu.getPreferredSize().getHeight();
			zoomMenu.show(zoomButton, x, y);
		});
		
		add(zoomButton, BorderLayout.EAST);

		this.setBorder(new MatteBorder(1, 0, 0, 0, Stratus.getTheme().getWidgetBorder()));
		
		
		
	}
	
	public void setStatus(String text) {
		status.setText(text);
	}
	
	public void showValueAtCoord(Coord<Integer> mapCoord) {
		setStatus(controller.getFitting().getInfoAtPoint(mapCoord));
	}
	
}
