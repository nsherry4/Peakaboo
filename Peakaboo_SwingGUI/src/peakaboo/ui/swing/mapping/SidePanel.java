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

import peakaboo.controller.mapper.MapController;
import peakaboo.datatypes.eventful.PeakabooSimpleListener;
import peakaboo.ui.swing.PeakabooMapperSwing;
import peakaboo.ui.swing.mapping.views.ViewsContainer;
import peakaboo.ui.swing.widgets.ImageButton;
import peakaboo.ui.swing.widgets.Spacing;
import peakaboo.ui.swing.widgets.pictures.SavePicture;



public class SidePanel extends JPanel
{

	protected MapController		controller;

	private JSpinner			shadesSpinner;
	private JCheckBox			contours;
	private JSpinner			width;
	private JSpinner			height;
	private JSpinner			interpolation;

	private PeakabooMapperSwing	owner;

	public final static boolean	SHOW_UI_FRAME_BORDERS	= true;


	public SidePanel(MapController controller, PeakabooMapperSwing owner)
	{

		this.controller = controller;
		this.owner = owner;

		createControls();

	}


	private void actionSavePicture()
	{

		if (owner.savePictureFolder == null) owner.savePictureFolder = owner.dataSourceFolder;
		owner.savePictureFolder = new SavePicture(owner, controller, owner.savePictureFolder).getStartingFolder();

	}


	private void createControls()
	{

		setBorder(Spacing.bSmall());
		setLayout(new GridBagLayout());

		GridBagConstraints maingbc = new GridBagConstraints();
		maingbc.insets = Spacing.iTiny();
		maingbc.ipadx = 0;
		maingbc.ipady = 0;

		// map settings
		maingbc.gridx = 0;
		maingbc.gridy = 0;
		maingbc.weightx = 0.0;
		maingbc.weighty = 0.0;
		maingbc.fill = GridBagConstraints.HORIZONTAL;
		add(createMapOptions(), maingbc);

		// map scale mode selector
		maingbc.gridy += 1;
		maingbc.weightx = 0.0;
		maingbc.weighty = 1.0;
		maingbc.fill = GridBagConstraints.BOTH;
		add(new ViewsContainer(controller), maingbc);

		// elements list
		/*
		 * maingbc.gridy += 1; maingbc.weightx = 0.0; maingbc.weighty = 1.0; maingbc.fill = GridBagConstraints.BOTH;
		 * elementsListPanel = createElementsList(); add(elementsListPanel, maingbc);
		 */

		ImageButton savePictures = new ImageButton("picture", "Save as Image", "Save current map as image", true);
		savePictures.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				actionSavePicture();
			}
		});
		maingbc.gridy += 1;
		maingbc.weightx = 0.0;
		maingbc.weighty = 0.0;
		maingbc.fill = GridBagConstraints.HORIZONTAL;
		add(savePictures, maingbc);

		controller.addListener(new PeakabooSimpleListener() {

			public void change()
			{

				width.setEnabled(!controller.dimensionsProvided());
				height.setEnabled(!controller.dimensionsProvided());

				shadesSpinner.setValue(controller.getSpectrumSteps());
				shadesSpinner.setEnabled(controller.getContours());
				contours.setSelected(controller.getContours());
				width.setValue(controller.getDataWidth());
				height.setValue(controller.getDataHeight());
				interpolation.setValue(controller.getInterpolation());

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
		width.setValue(controller.getDataWidth());
		width.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				controller.setDataWidth((Integer) ((JSpinner) e.getSource()).getValue());
			}
		});

		c.gridx = 0;
		mapProperties.add(new JLabel("Width:"), c);
		c.gridx = 1;
		mapProperties.add(width, c);

		c.gridy += 1;
		height = new JSpinner();
		height.setValue(controller.getDataHeight());
		height.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				controller.setDataHeight((Integer) ((JSpinner) e.getSource()).getValue());
			}
		});

		c.gridx = 0;
		mapProperties.add(new JLabel("Height:"), c);
		c.gridx = 1;
		mapProperties.add(height, c);

		c.gridy += 1;
		interpolation = new JSpinner();
		interpolation.setValue(controller.getInterpolation());
		interpolation.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				controller.setInterpolation((Integer) ((JSpinner) e.getSource()).getValue());
			}
		});
		c.gridx = 0;
		mapProperties.add(new JLabel("Interpolation Passes:"), c);
		c.gridx = 1;
		mapProperties.add(interpolation, c);

		c.gridy += 1;
		contours = new JCheckBox("Contours");
		contours.setSelected(controller.getContours());
		contours.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e)
			{
				controller.setContours(((JCheckBox) e.getSource()).isSelected());
			}
		});

		shadesSpinner = new JSpinner();
		shadesSpinner.setValue(controller.getSpectrumSteps());
		shadesSpinner.setEnabled(controller.getContours());
		shadesSpinner.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e)
			{
				controller.setSpectrumSteps((Integer) ((JSpinner) e.getSource()).getValue());
			}
		});
		c.gridx = 0;
		mapProperties.add(contours, c);
		c.gridx = 1;
		mapProperties.add(shadesSpinner, c);

		return mapProperties;

	}

}
