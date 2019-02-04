package org.peakaboo.ui.swing.mapping.sidebar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import org.peakaboo.controller.mapper.MappingController;

import swidget.widgets.Spacing;

public class MapAppearancePanel extends JPanel {

	private JSpinner shadesSpinner;
	private JCheckBox contours;
	
	public MapAppearancePanel(MappingController controller) {
		
		setName("Appearance");
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(Spacing.tiny, Spacing.medium, Spacing.tiny, Spacing.tiny);
		c.ipadx = 0;
		c.ipady = 0;

		c.weightx = 0.0;
		c.weighty = 1.0;
		c.gridy = 0;


		contours = new JCheckBox("Contours");
		contours.setSelected(controller.getSettings().getContours());
		contours.addActionListener(e -> {
			controller.getSettings().setContours(((JCheckBox) e.getSource()).isSelected());
		});

		shadesSpinner = new JSpinner();
		shadesSpinner.setValue(controller.getSettings().getSpectrumSteps());
		shadesSpinner.setEnabled(controller.getSettings().getContours());
		shadesSpinner.addChangeListener(e -> {
			controller.getSettings().setSpectrumSteps((Integer) ((JSpinner) e.getSource()).getValue());
		});
		
		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 1.0;
		this.add(contours, c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.weightx = 0.0;
		
		this.add(shadesSpinner, c);

		
		controller.addListener(e -> {
			shadesSpinner.setValue(controller.getSettings().getSpectrumSteps());
			shadesSpinner.setEnabled(controller.getSettings().getContours());
			contours.setSelected(controller.getSettings().getContours());
		});
		
	}
	
}
