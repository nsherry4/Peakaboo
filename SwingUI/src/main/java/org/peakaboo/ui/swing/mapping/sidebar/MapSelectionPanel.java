package org.peakaboo.ui.swing.mapping.sidebar;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.peakaboo.controller.mapper.MappingController;

import swidget.widgets.layout.SettingsPanel;

public class MapSelectionPanel extends SettingsPanel {

	private JSpinner threshold;
	private JSpinner padding;
	
	
	
	public MapSelectionPanel(MappingController controller) {
		
		setName("Selection");
		
		String thresholdTip = "<html>Controls the selection threshold.<br/>Points selected will be between (v/threshold, v*threshold),<br/>where v is the value of the clicked point.</html>";
		SpinnerNumberModel thresholdModel = new SpinnerNumberModel(controller.getSettings().getPointsSelection().getThreshold(), 1.0d, 100.0d, 0.1d);
		threshold = new JSpinner(thresholdModel);
		JLabel thresholdLabel = new JLabel("Threshold");
		threshold.addChangeListener(l -> {
			Double val = (Double) thresholdModel.getValue();
			controller.getSettings().getPointsSelection().setThreshold(val.floatValue());
		});
		threshold.setToolTipText(thresholdTip);
		thresholdLabel.setToolTipText(thresholdTip);
		
		padding = new JSpinner(new SpinnerNumberModel(controller.getSettings().getPointsSelection().getPadding(), 0, 10, 1));
		padding.addChangeListener(l -> {
			controller.getSettings().getPointsSelection().setPadding((Integer) padding.getValue());
		});
		
		JLabel paddingLabel = new JLabel("Padding");
		addSetting(threshold, thresholdLabel, LabelPosition.BESIDE, false, false);
		addSetting(padding, paddingLabel, LabelPosition.BESIDE, false, false);
		
		controller.addListener(s -> {
			boolean enabled = controller.getSettings().getView().getInterpolation() == 0;
			this.setEnabled(enabled);
			threshold.setEnabled(enabled);
			padding.setEnabled(enabled);
			thresholdLabel.setEnabled(enabled);
			paddingLabel.setEnabled(enabled);
		});
		
	}
	
}
