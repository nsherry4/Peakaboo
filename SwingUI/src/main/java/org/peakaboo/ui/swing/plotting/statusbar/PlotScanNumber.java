package org.peakaboo.ui.swing.plotting.statusbar;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.view.ChannelCompositeMode;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.ClearPanel;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentToggleButton;

public class PlotScanNumber extends ClearPanel {

	PlotController controller;
	
	private JSpinner scanNo;
	private JLabel scanLabel;
	private FluentToggleButton scanBlock;
	
	public PlotScanNumber(PlotController controller) {
		this.controller = controller;
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		scanNo = new JSpinner();
		scanNo.getEditor().setPreferredSize(new Dimension(50, 0));
		scanLabel = new JLabel("Scan");
		scanLabel.setBorder(Spacing.bSmall());
		scanBlock = new FluentToggleButton(StockIcon.CHOOSE_CANCEL)
				.withTooltip("Flag this scan to exclude it and extrapolate it from neighbouring points in maps")
				.withAction(selected -> {
					if (selected) {
						controller.data().getDiscards().discard(controller.view().getScanNumber());
					} else {
						controller.data().getDiscards().undiscard(controller.view().getScanNumber());
					}
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
			if (controller.view().getChannelCompositeMode() == ChannelCompositeMode.NONE) {
				scanNo.setValue(controller.view().getScanNumber() + 1);
				scanBlock.setSelected(controller.data().getDiscards().isDiscarded(controller.view().getScanNumber()));
				this.setEnabled(true);
			} else {
				this.setEnabled(false);
			}

		}
				
	}

	
}
