package org.peakaboo.ui.swing.mapping;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

import org.peakaboo.controller.mapper.MappingController;

import cyclops.Coord;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;
import swidget.widgets.ZoomSlider;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ImageButtonLayout;

class MapStatusBar extends JPanel {

	private JLabel status;
	private MappingController controller;
	
	MapStatusBar(MapperPanel tabPanel, MappingController controller) {
		
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
			controller.getSettings().getView().setZoom(value / 10f);
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

		
		Color dividerColour = UIManager.getColor("stratus-widget-border");
		if (dividerColour == null) {
			dividerColour = Color.LIGHT_GRAY;
		}
		this.setBorder(new MatteBorder(1, 0, 0, 0, dividerColour));
		
		
		
	}
	
	public void setStatus(String text) {
		status.setText(text);
	}
	
	public void showValueAtCoord(Coord<Integer> mapCoord)
	{
		String noValue = "Index: -, X: -, Y: -, Value: -";

		if (mapCoord == null)
		{
			setStatus(noValue);
			return;
		}

		int index = (mapCoord.y * controller.getSettings().getView().getDataWidth() + mapCoord.x) + 1;
		
		if (controller.getSettings().getView().isValidPoint(mapCoord))
		{
			String value = controller.getSettings().getMapFittings().getIntensityMeasurementAtPoint(mapCoord);
			if (controller.getSettings().getView().getInterpolation() != 0) value += " (not interpolated)";
			
			setStatus("Index: " + index + ", X: " + (mapCoord.x + 1) + ", Y: " + (mapCoord.y + 1) + ", Value: "
					+ value);
		}
		else
		{
			setStatus(noValue);
		}

	}
	
}
