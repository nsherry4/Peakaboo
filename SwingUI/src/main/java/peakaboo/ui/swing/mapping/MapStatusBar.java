package peakaboo.ui.swing.mapping;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import peakaboo.controller.mapper.MappingController;
import scitypes.Coord;
import swidget.widgets.Spacing;
import swidget.widgets.ZoomSlider;

class MapStatusBar extends JPanel {

	private JLabel status;
	private MappingController controller;
	private MapperPanel tabPanel;
	
	MapStatusBar(MapperPanel tabPanel, MappingController controller) {
		
		this.controller = controller;
		this.tabPanel = tabPanel;
		
		setLayout(new BorderLayout());
		
		
		//status text
		status = new JLabel("");
		status.setBorder(Spacing.bSmall());
		status.setHorizontalAlignment(JLabel.CENTER);
		status.setFont(status.getFont().deriveFont(Font.PLAIN));
		
		showValueAtCoord(null);
		
		add(status, BorderLayout.CENTER);
		
		
		
		//zoom controls
		ZoomSlider zoom = new ZoomSlider(10, 100, 1);
		add(zoom, BorderLayout.EAST);
		zoom.addListener(() -> {
			controller.getSettings().getView().setZoom(zoom.getValue()/10f);
		});
		
		
		
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

		int index = mapCoord.y * controller.getSettings().getView().getDataWidth() + mapCoord.x;
		index++;
		
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
