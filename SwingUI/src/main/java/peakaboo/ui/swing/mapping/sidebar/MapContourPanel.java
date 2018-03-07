package peakaboo.ui.swing.mapping.sidebar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;

import eventful.EventfulTypeListener;
import peakaboo.controller.mapper.MappingController;
import peakaboo.ui.swing.mapping.MapperPanel;
import peakaboo.ui.swing.mapping.MapperViewPanel;
import swidget.widgets.Spacing;

public class MapContourPanel extends JPanel {

	private JSpinner shadesSpinner;
	private JCheckBox contours;
	private JSpinner interpolation;
	
	public MapContourPanel(MapperPanel tabPanel, MappingController controller) {
		
		
		if (MapperViewPanel.SHOW_UI_FRAME_BORDERS) this.setBorder(new TitledBorder("Appearance"));
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = Spacing.iTiny();
		c.ipadx = 0;
		c.ipady = 0;

		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridy = 0;
		
		interpolation = new JSpinner();
		interpolation.setValue(controller.settings.getInterpolation());
		interpolation.addChangeListener(e -> {
			controller.settings.setInterpolation((Integer) ((JSpinner) e.getSource()).getValue());
		});
		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 1.0;
		this.add(new JLabel("Interpolation Passes:"), c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.weightx = 0.0;
		this.add(interpolation, c);

		c.gridy += 1;
		contours = new JCheckBox("Contours");
		contours.setSelected(controller.settings.getContours());
		contours.addActionListener(e -> {
			controller.settings.setContours(((JCheckBox) e.getSource()).isSelected());
		});

		shadesSpinner = new JSpinner();
		shadesSpinner.setValue(controller.settings.getSpectrumSteps());
		shadesSpinner.setEnabled(controller.settings.getContours());
		shadesSpinner.addChangeListener(e -> {
			controller.settings.setSpectrumSteps((Integer) ((JSpinner) e.getSource()).getValue());
		});
		
		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.0;
		this.add(contours, c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.weightx = 1.0;
		this.add(shadesSpinner, c);

		
		controller.addListener(e -> {
			shadesSpinner.setValue(controller.settings.getSpectrumSteps());
			shadesSpinner.setEnabled(controller.settings.getContours());
			contours.setSelected(controller.settings.getContours());
			interpolation.setValue(controller.settings.getInterpolation());
		});
		
	}
	
}
