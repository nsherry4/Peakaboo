package org.peakaboo.ui.swing.mapping.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.swidget.Swidget;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.ZoomSlider;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButton;
import org.peakaboo.framework.swidget.widgets.buttons.ImageButtonLayout;
import org.peakaboo.ui.swing.mapping.MapperPanel;

public class MapStatusBar extends JPanel {

	private JLabel status;
	private MappingController controller;
	
	public MapStatusBar(MapperPanel tabPanel, MappingController controller) {
		
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
		ZoomSlider zoom = new ZoomSlider(10, 100, 1, value -> {
			controller.getSettings().setZoom(value / 10f);
		});
		zoom.setOpaque(false);
		zoom.setBorder(Spacing.bMedium());
		
		JPopupMenu zoomMenu = new JPopupMenu();
		zoomMenu.setBorder(Spacing.bNone());
		zoomMenu.add(zoom);
		ImageButton zoomButton = new ImageButton(StockIcon.FIND).withTooltip("Zoom").withLayout(ImageButtonLayout.IMAGE).withBordered(false);
		zoomButton.addActionListener(e -> {
			zoomMenu.show(zoomButton, (int)((-zoomMenu.getPreferredSize().getWidth()+zoomButton.getSize().getWidth())/2f), (int)-zoomMenu.getPreferredSize().getHeight());
		});
		
		add(zoomButton, BorderLayout.EAST);

		this.setBorder(new MatteBorder(1, 0, 0, 0, Swidget.dividerColor()));
		
		
		
	}
	
	public void setStatus(String text) {
		status.setText(text);
	}
	
	public void showValueAtCoord(Coord<Integer> mapCoord) {
		setStatus(controller.getFitting().getInfoAtPoint(mapCoord));
	}
	
}
