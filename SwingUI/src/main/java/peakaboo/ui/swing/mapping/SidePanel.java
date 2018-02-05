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

import eventful.EventfulTypeListener;
import peakaboo.controller.mapper.MappingController;
import peakaboo.ui.swing.mapping.views.ViewsContainer;
import scitypes.Coord;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ImageButton;
import swidget.widgets.ImageButton.Layout;
import swidget.widgets.Spacing;



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
		add(createMapDimensions(), maingbc);

		// map settings
		maingbc.gridy++;
		maingbc.weighty = 0.0;
		maingbc.fill = GridBagConstraints.HORIZONTAL;
		add(createMapOptions(), maingbc);
		
		// map scale mode selector
		maingbc.gridy += 1;
		maingbc.weighty = 1.0;
		maingbc.fill = GridBagConstraints.BOTH;
		add(new ViewsContainer(controller.getDisplay()), maingbc);

		
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

				shadesSpinner.setValue(controller.settings.getSpectrumSteps());
				shadesSpinner.setEnabled(controller.settings.getContours());
				contours.setSelected(controller.settings.getContours());
				//contours.setEnabled(controller.getActiveTabModel().displayMode == MapDisplayMode.COMPOSITE);
				width.setValue(controller.settings.getDataWidth());
				height.setValue(controller.settings.getDataHeight());
				interpolation.setValue(controller.settings.getInterpolation());

			}
		});

	}


	private JPanel createMapOptions()
	{

		JPanel mapProperties = new JPanel();
		if (SHOW_UI_FRAME_BORDERS) mapProperties.setBorder(new TitledBorder("Appearance"));
		
		mapProperties.setLayout(new GridBagLayout());
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
		mapProperties.add(new JLabel("Interpolation Passes:"), c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.weightx = 0.0;
		mapProperties.add(interpolation, c);

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
		mapProperties.add(contours, c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		c.weightx = 1.0;
		mapProperties.add(shadesSpinner, c);

		return mapProperties;

	}
	
	

	private JPanel createMapDimensions()
	{

		JPanel mapProperties = new JPanel();
		if (SHOW_UI_FRAME_BORDERS) mapProperties.setBorder(new TitledBorder("Dimensions"));
		
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
		width.setValue(controller.settings.getDataWidth());
		width.addChangeListener(e -> {
			controller.settings.setDataWidth((Integer) ((JSpinner) e.getSource()).getValue());
		});

		c.gridx = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.LINE_START;
		mapProperties.add(new JLabel("Width:"), c);
		c.gridx = 1;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.LINE_END;
		mapProperties.add(width, c);

		c.gridy += 1;
		height = new JSpinner();
		height.setValue(controller.settings.getDataHeight());
		height.addChangeListener(e -> {
			controller.settings.setDataHeight((Integer) ((JSpinner) e.getSource()).getValue());
		});

		c.gridx = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.LINE_START;
		mapProperties.add(new JLabel("Height:"), c);
		c.gridx = 1;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.LINE_END;
		mapProperties.add(height, c);
		
		
		if (!controller.mapsController.isDimensionsProvided()) {
			ImageButton magic = new ImageButton("auto", "", "Try to detect the map's dimensions.", Layout.IMAGE, IconSize.TOOLBAR_SMALL);
			c.gridx = 0;
			c.gridwidth = 2;
			c.gridy += 1;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.LINE_END;
			magic.addActionListener(e -> {
				Coord<Integer> guess = controller.mapsController.guessDataDimensions();
				if (guess != null) {
					height.setValue(1);
					width.setValue(1);
					height.setValue(guess.y);
					width.setValue(guess.x);
				}
			});
			mapProperties.add(magic, c);
		} else {
			ImageButton reset = new ImageButton(StockIcon.ACTION_REFRESH, "", "Reset the dimensions to those given in the data set.", Layout.IMAGE, IconSize.TOOLBAR_SMALL);
			c.gridx = 0;
			c.gridwidth = 2;
			c.gridy += 1;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.LINE_END;
			reset.addActionListener(e -> {
				height.setValue(controller.mapsController.getOriginalDataHeight());
				width.setValue(controller.mapsController.getOriginalDataWidth());
			});
			mapProperties.add(reset, c);
		}
		c.gridwidth = 1;

		return mapProperties;

	}

}
