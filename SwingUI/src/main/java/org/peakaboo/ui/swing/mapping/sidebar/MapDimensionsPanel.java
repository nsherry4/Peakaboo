package org.peakaboo.ui.swing.mapping.sidebar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.plural.monitor.TaskMonitor.Event;
import org.peakaboo.framework.plural.monitor.swing.TaskMonitorPanel;
import org.peakaboo.framework.plural.monitor.swing.TaskMonitorView;
import org.peakaboo.framework.plural.streams.StreamExecutor;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButton;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButtonLayout;
import org.peakaboo.framework.swidget.widgets.layerpanel.LayerPanel;
import org.peakaboo.framework.swidget.widgets.layerpanel.ModalLayer;

public class MapDimensionsPanel extends JPanel {

	private JSpinner width;
	private JSpinner height;
	
	//either guess or reset, depending on if we have the original dimensions or not
	private FluentButton magic; 
	
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
			magic = new FluentButton(compact ? "Guess" : "Guess Dimensions")
					.withIcon("auto", IconSize.TOOLBAR_SMALL)
					.withTooltip("Try to detect the map's dimensions.")
					.withLayout(FluentButtonLayout.IMAGE_ON_SIDE)
					.withBordered(false);
			c.gridx = 0;
			c.gridwidth = 2;
			c.gridy += 1;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.LINE_END;
			magic.addActionListener(e -> {
				StreamExecutor<Coord<Integer>> guessTask = controller.getUserDimensions().guessDataDimensions();
				TaskMonitorView view = new TaskMonitorView(guessTask);
				TaskMonitorPanel panel = new TaskMonitorPanel("Detecting Dimensions", view);
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
			magic = new FluentButton(StockIcon.ACTION_REFRESH, IconSize.TOOLBAR_SMALL)
					.withTooltip("Reset the dimensions to those given in the data set.")
					.withLayout(FluentButtonLayout.IMAGE)
					.withBordered(false);
			c.gridx = 0;
			c.gridwidth = 2;
			c.gridy += 1;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.LINE_END;
			magic.addActionListener(e -> {
				height.setValue(controller.rawDataController.getOriginalDataHeight());
				width.setValue(controller.rawDataController.getOriginalDataWidth());
			});
			this.add(magic, c);
		}
		c.gridwidth = 1;
		
		
		
		controller.addListener(t -> {
			width.setValue(controller.getUserDimensions().getUserDataWidth());
			height.setValue(controller.getUserDimensions().getUserDataHeight());
		});
		
	}

	public FluentButton getMagicDimensionsButton() {
		return magic;
	}
	
	
	
}
