package peakaboo.ui.swing.mapping.sidebar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import cyclops.Coord;
import peakaboo.controller.mapper.MappingController;
import peakaboo.ui.swing.mapping.MapperPanel;
import plural.streams.StreamExecutor;
import plural.streams.StreamExecutor.Event;
import plural.streams.swing.StreamExecutorPanel;
import plural.streams.swing.StreamExecutorView;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.layerpanel.ModalLayer;
import swidget.widgets.Spacing;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ImageButtonLayout;

public class MapDimensionsPanel extends JPanel {

	private JSpinner width;
	private JSpinner height;
	
	public MapDimensionsPanel(MapperPanel tabPanel, MappingController controller) {
				
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
		width.setValue(controller.getSettings().getView().getDataWidth());
		width.addChangeListener(e -> {
			controller.getSettings().getView().setDataWidth((Integer) ((JSpinner) e.getSource()).getValue());
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
		height.setValue(controller.getSettings().getView().getDataHeight());
		height.addChangeListener(e -> {
			controller.getSettings().getView().setDataHeight((Integer) ((JSpinner) e.getSource()).getValue());
		});

		c.gridx = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(new JLabel("Height:"), c);
		c.gridx = 1;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.LINE_END;
		this.add(height, c);
		
		
		if (!controller.mapsController.hasOriginalDataDimensions()) {
			ImageButton magic = new ImageButton("Guess Dimensions")
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
				StreamExecutor<Coord<Integer>> guessTask = controller.mapsController.guessDataDimensions();
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
				height.setValue(controller.mapsController.getOriginalDataHeight());
				width.setValue(controller.mapsController.getOriginalDataWidth());
			});
			this.add(reset, c);
		}
		c.gridwidth = 1;
		
		
		
		controller.addListener(e -> {
			width.setValue(controller.getSettings().getView().getDataWidth());
			height.setValue(controller.getSettings().getView().getDataHeight());
		});
		
	}
	
}
