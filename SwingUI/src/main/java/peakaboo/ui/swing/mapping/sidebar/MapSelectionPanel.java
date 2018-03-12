package peakaboo.ui.swing.mapping.sidebar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import peakaboo.controller.mapper.MappingController;
import peakaboo.ui.swing.mapping.MapperPanel;
import peakaboo.ui.swing.mapping.MapperSidebar;
import swidget.widgets.SettingsPanel;

public class MapSelectionPanel extends SettingsPanel {

	JSpinner threshold;
	JSpinner padding;
	
	
	
	public MapSelectionPanel(MapperPanel tabPanel, MappingController controller) {
		if (MapperSidebar.SHOW_UI_FRAME_BORDERS) this.setBorder(new TitledBorder("Selection"));
		
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
		
		addSetting(threshold, thresholdLabel, LabelPosition.BESIDE, false, false);
		addSetting(padding, "Padding", LabelPosition.BESIDE, false, false);
		
	}
	
}
