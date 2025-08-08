package org.peakaboo.ui.swing.plotting.statusbar;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JSpinner;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.view.mode.SingleViewMode;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.panels.ClearPanel;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonConfig.BorderStyle;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentToggleButton;

public class PlotScanNumber extends ClearPanel {

	PlotController controller;
	
	private JSpinner scanNo;
	private JLabel scanLabel;
	private FluentToggleButton scanBlock;
	
	public PlotScanNumber(PlotController controller) {
		this.controller = controller;
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		scanNo = new JSpinner();
		scanNo.getEditor().setPreferredSize(new Dimension(60, 0));
		scanLabel = new JLabel("Scan");
		scanLabel.setBorder(Spacing.bSmall());
		scanBlock = new FluentToggleButton(StockIcon.CHOOSE_CANCEL)
				.withTooltip("Flag this scan to exclude it and extrapolate it from neighbouring points in maps")
				.withBordered(BorderStyle.ACTIVE)
				.withAction(selected -> {
					int scanNumber = controller.view().getScanNumber();
					controller.data().getDiscards().setDiscarded(scanNumber, selected);
				});
		
		this.add(scanLabel);
		this.add(Box.createHorizontalStrut(2));
		this.add(scanNo);
		this.add(scanBlock);
		this.add(Box.createHorizontalStrut(4));
		

		scanNo.addChangeListener(e -> {
			JSpinner scan = (JSpinner) e.getSource();
			int value = (Integer) ((scan).getValue());
			controller.view().setScanNumber(value - 1);
		});
		

		scanBlock.setFocusable(false);
		
		
	}
	
	void setWidgetState(boolean hasData) {
		
		if (hasData) {
			if (controller.view().getChannelViewMode().equals(new SingleViewMode())) {
				scanNo.setValue(controller.view().getScanNumber() + 1);
				scanBlock.setSelected(controller.data().getDiscards().isDiscarded(controller.view().getScanNumber()));
				this.setVisible(true);
				/*
				 * currently bad scans are tracked by their order in a list of good scans, but
				 * this makes it difficult to link a scan index to the right point in a map when
				 * it comes time for interpolation.
				 */
				scanBlock.setVisible(controller.data().getDataSet().getDataSource().isRectangular());
			} else {
				this.setVisible(false);
			}

		}
				
	}

	
}
