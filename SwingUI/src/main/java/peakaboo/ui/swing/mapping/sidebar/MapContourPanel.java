package peakaboo.ui.swing.mapping.sidebar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import peakaboo.controller.mapper.MappingController;
import swidget.widgets.Spacing;

public class MapContourPanel extends JPanel {

	private JSpinner shadesSpinner;
	private JCheckBox contours;
	private JSpinner interpolation;
	
	public MapContourPanel(MappingController controller) {
		
		setName("Appearance");
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(Spacing.tiny, Spacing.medium, Spacing.tiny, Spacing.tiny);
		c.ipadx = 0;
		c.ipady = 0;

		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridy = 0;
		
		interpolation = new JSpinner();
		interpolation.setValue(controller.getSettings().getView().getInterpolation());
		interpolation.addChangeListener(e -> {
			controller.getSettings().getView().setInterpolation((Integer) ((JSpinner) e.getSource()).getValue());
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
		contours.setSelected(controller.getSettings().getView().getContours());
		contours.addActionListener(e -> {
			controller.getSettings().getView().setContours(((JCheckBox) e.getSource()).isSelected());
		});

		shadesSpinner = new JSpinner();
		shadesSpinner.setValue(controller.getSettings().getView().getSpectrumSteps());
		shadesSpinner.setEnabled(controller.getSettings().getView().getContours());
		shadesSpinner.addChangeListener(e -> {
			controller.getSettings().getView().setSpectrumSteps((Integer) ((JSpinner) e.getSource()).getValue());
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
			shadesSpinner.setValue(controller.getSettings().getView().getSpectrumSteps());
			shadesSpinner.setEnabled(controller.getSettings().getView().getContours());
			contours.setSelected(controller.getSettings().getView().getContours());
			interpolation.setValue(controller.getSettings().getView().getInterpolation());
		});
		
	}
	
}
