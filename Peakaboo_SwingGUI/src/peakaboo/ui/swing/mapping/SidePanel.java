package peakaboo.ui.swing.mapping;



import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import peakaboo.controller.mapper.MappingController;
import peakaboo.ui.swing.mapping.views.ViewsContainer;
import swidget.widgets.Spacing;
import eventful.EventfulTypeListener;



public class SidePanel extends JPanel
{

	protected MappingController		controller;
	

	private JSpinner			shadesSpinner;
	private JCheckBox			contours;
	private JSpinner			width;
	private JSpinner			height;
	private JSpinner			interpolation;


	public final static boolean	SHOW_UI_FRAME_BORDERS	= true;


	public SidePanel(MappingController controller)
	{

		this.controller = controller;

		createControls();

	}


	private void createControls()
	{

		setBorder(Spacing.bSmall());
		setLayout(new GridBagLayout());

		
		
		GridBagConstraints maingbc = new GridBagConstraints();
		maingbc.insets = Spacing.iTiny();
		maingbc.ipadx = 0;
		maingbc.ipady = 0;
		maingbc.weightx = 1;

		
		// map settings
		maingbc.gridx = 0;
		maingbc.gridy = 0;
		maingbc.weighty = 0.0;
		maingbc.fill = GridBagConstraints.HORIZONTAL;
		add(createMapOptions(), maingbc);

		// map scale mode selector
		maingbc.gridy += 1;
		maingbc.weighty = 1.0;
		maingbc.fill = GridBagConstraints.BOTH;
		add(new ViewsContainer(controller.getActiveTabController()), maingbc);

		
		// elements list
		/*
		 * maingbc.gridy += 1; maingbc.weightx = 0.0; maingbc.weighty = 1.0; maingbc.fill = GridBagConstraints.BOTH;
		 * elementsListPanel = createElementsList(); add(elementsListPanel, maingbc);
		 */
		
		controller.addListener(new EventfulTypeListener<String>() {

			public void change(String s)
			{

				//sometimes the dimensions in the file could be wrong
				//width.setEnabled(!controller.mapsController.isDimensionsProvided());
				//height.setEnabled(!controller.mapsController.isDimensionsProvided());

				shadesSpinner.setValue(controller.mapsController.getSpectrumSteps());
				shadesSpinner.setEnabled(controller.mapsController.getContours());
				contours.setSelected(controller.mapsController.getContours());
				//contours.setEnabled(controller.getActiveTabModel().displayMode == MapDisplayMode.COMPOSITE);
				width.setValue(controller.mapsController.getDataWidth());
				height.setValue(controller.mapsController.getDataHeight());
				interpolation.setValue(controller.mapsController.getInterpolation());

			}
		});

	}


	private JPanel createMapOptions()
	{

		JPanel mapProperties = new JPanel();
		if (SHOW_UI_FRAME_BORDERS) mapProperties.setBorder(new TitledBorder("Map Settings"));
		
		mapProperties.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = Spacing.iTiny();
		c.ipadx = 0;
		c.ipady = 0;

		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridy = 0;
		width = new JSpinner();
		width.setValue(controller.mapsController.getDataWidth());
		width.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				controller.mapsController.setDataWidth((Integer) ((JSpinner) e.getSource()).getValue());
			}
		});

		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		mapProperties.add(new JLabel("Width:"), c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		mapProperties.add(width, c);

		c.gridy += 1;
		height = new JSpinner();
		height.setValue(controller.mapsController.getDataHeight());
		height.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				controller.mapsController.setDataHeight((Integer) ((JSpinner) e.getSource()).getValue());
			}
		});

		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		mapProperties.add(new JLabel("Height:"), c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		mapProperties.add(height, c);

		c.gridy += 1;
		interpolation = new JSpinner();
		interpolation.setValue(controller.mapsController.getInterpolation());
		interpolation.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				controller.mapsController.setInterpolation((Integer) ((JSpinner) e.getSource()).getValue());
			}
		});
		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		mapProperties.add(new JLabel("Interpolation Passes:"), c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		mapProperties.add(interpolation, c);

		c.gridy += 1;
		contours = new JCheckBox("Contours");
		contours.setSelected(controller.mapsController.getContours());
		contours.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.mapsController.setContours(((JCheckBox) e.getSource()).isSelected());
			}
		});

		shadesSpinner = new JSpinner();
		shadesSpinner.setValue(controller.mapsController.getSpectrumSteps());
		shadesSpinner.setEnabled(controller.mapsController.getContours());
		shadesSpinner.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				controller.mapsController.setSpectrumSteps((Integer) ((JSpinner) e.getSource()).getValue());
			}
		});
		
		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		mapProperties.add(contours, c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		mapProperties.add(shadesSpinner, c);

		return mapProperties;

	}

}
