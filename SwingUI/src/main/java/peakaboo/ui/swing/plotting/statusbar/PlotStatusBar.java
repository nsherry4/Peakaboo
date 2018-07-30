package peakaboo.ui.swing.plotting.statusbar;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import peakaboo.controller.plotter.PlotController;
import swidget.widgets.ClearPanel;
import swidget.widgets.Spacing;

public class PlotStatusBar extends ClearPanel {


	private JLabel channelLabel;
	private PlotZoomControls zoom;
	private PlotScanNumber scanSelector;

	public PlotStatusBar(PlotController controller) {

		channelLabel = new JLabel("");
		channelLabel.setHorizontalAlignment(SwingConstants.CENTER);
		channelLabel.setFont(channelLabel.getFont().deriveFont(Font.PLAIN));

		this.setBorder(Spacing.bTiny());
		this.setLayout(new BorderLayout());

		channelLabel.setBorder(Spacing.bSmall());
		this.add(channelLabel, BorderLayout.CENTER);

		zoom = new PlotZoomControls(controller);
		this.add(zoom, BorderLayout.EAST);

		scanSelector = new PlotScanNumber(controller);
		this.add(scanSelector, BorderLayout.WEST);



	}
	
	public void setWidgetState(boolean hasData) {
		this.setEnabled(hasData);
		
		scanSelector.setWidgetState(hasData);
		zoom.setWidgetState(hasData);
		
	}


	public void setChannelText(String text) {
		channelLabel.setText(text);
	}

}
