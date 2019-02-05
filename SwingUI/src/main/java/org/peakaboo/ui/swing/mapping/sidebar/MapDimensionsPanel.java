package org.peakaboo.ui.swing.mapping.sidebar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;

import org.peakaboo.controller.mapper.MappingController;

import cyclops.Coord;
import plural.streams.StreamExecutor;
import plural.streams.StreamExecutor.Event;
import plural.streams.swing.StreamExecutorPanel;
import plural.streams.swing.StreamExecutorView;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ImageButtonLayout;
import swidget.widgets.layerpanel.LayerPanel;
import swidget.widgets.layerpanel.ModalLayer;

public class MapDimensionsPanel extends JPanel {

	private JSpinner width;
	private JSpinner height;
	
	public MapDimensionsPanel(LayerPanel tabPanel, MappingController controller) {
		this(tabPanel, controller, false);
	}
	
	public MapDimensionsPanel(LayerPanel tabPanel, MappingController controller, boolean compact) {
				
		setName("Dimensions");
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(Spacing.tiny, Spacing.medium, Spacing.tiny, Spacing.tiny);
		c.ipadx = 0;
		c.ipady = 0;

		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridy = 0;
		width = new JSpinner();
		width.setValue(controller.getUserDimensions().getUserDataWidth());
		width.addChangeListener(e -> {
			controller.getUserDimensions().setUserDataWidth((Integer) ((JSpinner) e.getSource()).getValue());
		});

		c.gridx = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(new JLabel("Width:"), c);
		c.gridx = 1;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.LINE_END;
		this.add(width, c);

		c.gridy += 1;
		height = new JSpinner();
		height.setValue(controller.getUserDimensions().getUserDataHeight());
		height.addChangeListener(e -> {
			controller.getUserDimensions().setUserDataHeight((Integer) ((JSpinner) e.getSource()).getValue());
		});

		c.gridx = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(new JLabel("Height:"), c);
		c.gridx = 1;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.LINE_END;
		this.add(height, c);
		
		
		if (!controller.rawDataController.hasOriginalDataDimensions()) {
			ImageButton magic = new ImageButton(compact ? "Guess" : "Guess Dimensions")
					.withIcon("auto", IconSize.TOOLBAR_SMALL)
					.withTooltip("Try to detect the map's dimensions.")
					.withLayout(ImageButtonLayout.IMAGE_ON_SIDE)
					.withBordered(false);
			c.gridx = 0;
			c.gridwidth = 2;
			c.gridy += 1;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.LINE_END;
			magic.addActionListener(e -> {
				StreamExecutor<Coord<Integer>> guessTask = controller.getUserDimensions().guessDataDimensions();
				StreamExecutorView view = new StreamExecutorView(guessTask);
				StreamExecutorPanel panel = new StreamExecutorPanel("Detecting Dimensions", view);
				ModalLayer layer = new ModalLayer(tabPanel, panel);
				guessTask.addListener(event -> {
					SwingUtilities.invokeLater(() -> {
						if (event == Event.ABORTED) {
							tabPanel.removeLayer(layer);
						}
						if (event == Event.COMPLETED) {
						
							tabPanel.removeLayer(layer);
							
							Coord<Integer> guess = guessTask.getResult().orElse(null);
							if (guess != null) {
								height.setValue(1);
								width.setValue(1);
								height.setValue(guess.y);
								width.setValue(guess.x);
							}							
						}
					});
				});
				tabPanel.pushLayer(layer);
				guessTask.start();				

			});
			this.add(magic, c);
		} else {
			ImageButton reset = new ImageButton(StockIcon.ACTION_REFRESH, IconSize.TOOLBAR_SMALL)
					.withTooltip("Reset the dimensions to those given in the data set.")
					.withLayout(ImageButtonLayout.IMAGE)
					.withBordered(false);
			c.gridx = 0;
			c.gridwidth = 2;
			c.gridy += 1;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.LINE_END;
			reset.addActionListener(e -> {
				height.setValue(controller.rawDataController.getOriginalDataHeight());
				width.setValue(controller.rawDataController.getOriginalDataWidth());
			});
			this.add(reset, c);
		}
		c.gridwidth = 1;
		
		
		
		controller.addListener(e -> {
			width.setValue(controller.getUserDimensions().getUserDataWidth());
			height.setValue(controller.getUserDimensions().getUserDataHeight());
		});
		
	}
	
}
