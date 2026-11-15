package org.peakaboo.ui.swing.mapping.sidebar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.dimensions.MapDimensionsController;
import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.accent.Mutable;
import org.peakaboo.framework.plural.monitor.TaskMonitor.Event;
import org.peakaboo.framework.plural.monitor.swing.TaskMonitorLayer;
import org.peakaboo.framework.plural.monitor.swing.TaskMonitorView;
import org.peakaboo.framework.plural.streams.StreamExecutor;
import org.peakaboo.framework.stratus.api.Spacing;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonLayout;
import org.peakaboo.framework.stratus.components.ui.layers.LayerPanel;
import org.peakaboo.ui.swing.app.PeakabooIcons;

public class MapDimensionsPanel extends JPanel {

	/** How long a guess can run before we show its progress */
	private static final int GUESS_PROGRESS_DELAY_MS = 500;

	//either guess or reset, depending on if we have the original dimensions or not
	private FluentButton magic, reset; 
	private JSpinner widthSpinner, heightSpinner;
	
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
		widthSpinner = new JSpinner();
		widthSpinner.setValue(controller.getUserDimensions().getUserDataWidth());
		widthSpinner.addChangeListener(e -> {
			//Why is this like this instead of just widthSpinner.getValue()?
			controller.getUserDimensions().setUserDataWidth((Integer) ((JSpinner) e.getSource()).getValue());
		});

		c.gridx = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(new JLabel("Width:"), c);
		c.gridx = 1;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.LINE_END;
		this.add(widthSpinner, c);

		c.gridy += 1;
		heightSpinner = new JSpinner();
		heightSpinner.setValue(controller.getUserDimensions().getUserDataHeight());
		heightSpinner.addChangeListener(e -> {
			controller.getUserDimensions().setUserDataHeight((Integer) ((JSpinner) e.getSource()).getValue());
		});

		c.gridx = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.LINE_START;
		this.add(new JLabel("Height:"), c);
		c.gridx = 1;
		c.weightx = 0.0;
		c.anchor = GridBagConstraints.LINE_END;
		this.add(heightSpinner, c);
		
		
		if (!controller.rawDataController.hasOriginalDataDimensions()) {

			c.gridy += 1;
			
			magic = makeGuessDimensionsButton(tabPanel, controller, compact);
			c.gridx = 0;
			c.gridwidth = 2;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.LINE_START;
			this.add(magic, c);

			
		} else {
			
			c.gridy += 1;
			
			magic = makeGuessDimensionsButton(tabPanel, controller, true);
			c.gridx = 0;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.LINE_START;
			this.add(magic, c);
			
			reset = makeResetDimensionsButton(controller);
			c.gridx = 1;
			c.weightx = 0.0;
			c.anchor = GridBagConstraints.LINE_END;
			this.add(reset, c);
			
		}
		c.gridwidth = 1;
		
		
		
		controller.addListener(t -> {
			widthSpinner.setValue(controller.getUserDimensions().getUserDataWidth());
			heightSpinner.setValue(controller.getUserDimensions().getUserDataHeight());
		});
		
	}

	public FluentButton getMagicDimensionsButton() {
		return magic;
	}

	public Optional<FluentButton> getResetDimensionsButton() {
		return Optional.ofNullable(reset);
	}
	
	
	
	private FluentButton makeGuessDimensionsButton(LayerPanel tabPanel, MappingController controller, boolean compact) {
		return new FluentButton(compact ? "Guess" : "Guess Dimensions")
				.withIcon(PeakabooIcons.AUTO, IconSize.BUTTON, Stratus.getTheme().getControlText())
				.withTooltip("Try to detect the map's dimensions.")
				.withLayout(FluentButtonLayout.IMAGE_ON_SIDE)
				.withBordered(false)
				.withAction(() -> guessDimensions(tabPanel, controller, guess -> {}));

	}

	/**
	 * Guesses the map's dimensions in the background, showing progress on the given
	 * panel if it takes a while, and applies the guess if there is one.
	 * @param onGuessed callback for guessed dimensions
	 */
	public static void guessDimensions(LayerPanel tabPanel, MappingController controller, Consumer<Coord<Integer>> onGuessed) {
		StreamExecutor<Coord<Integer>> guessTask = controller.getUserDimensions().guessDataDimensions();
		TaskMonitorView view = new TaskMonitorView(guessTask);
		TaskMonitorLayer layer = new TaskMonitorLayer(tabPanel, "Detecting Dimensions", view);

		// Most guesses finish quickly, so we only show progress for ones that don't.
		// Thread-safe since it's all on the event thread.
		Mutable<Boolean> finished = new Mutable<>(false);
		Timer showProgress = new Timer(GUESS_PROGRESS_DELAY_MS, e -> {
			if (!finished.get()) {
				tabPanel.pushLayer(layer);
			}
		});
		showProgress.setRepeats(false);

		guessTask.addListener(event -> {
			SwingUtilities.invokeLater(() -> {
				if (event == Event.ABORTED || event == Event.COMPLETED) {
					finished.set(true);
					showProgress.stop();
					// Does nothing if the layer was never shown
					tabPanel.removeLayer(layer);
				}
				if (event == Event.COMPLETED) {
					Coord<Integer> guess = guessTask.getResult().orElse(null);
					if (guess != null) {
						controller.getUserDimensions().setUserDimensions(guess);
						onGuessed.accept(controller.getUserDimensions().getDimensions());
					}
				}
			});
		});
		showProgress.start();
		guessTask.start();
	}

	/**
	 * Called when a map is opened with no dimensions available. If the data set doesn't
	 * declare usable dimensions, we guess them if certain performance conditions are met.
	 */
	public static void guessOnOpen(LayerPanel tabPanel, MappingController controller) {
		if (controller.getUserDimensions().hasUsableDeclaredDimensions()) {
			return;
		}
		if (controller.rawDataController.getMapSize() > MapDimensionsController.AUTO_GUESS_MAX_SCANS) {
			tabPanel.showToast("Map dimensions weren't provided by the data set.\nUse Guess in the dimensions settings to detect them.");
			return;
		}
		guessDimensions(tabPanel, controller, guess ->
			tabPanel.showToast("Map dimensions weren't provided, guessed " + guess.x + " × " + guess.y + ".\nCheck them in the dimensions settings.")
		);
	}
	
	private FluentButton makeResetDimensionsButton(MappingController controller) {
		return new FluentButton(StockIcon.ACTION_REFRESH_SYMBOLIC, IconSize.BUTTON)
				.withTooltip("Reset the dimensions to those given in the data set.")
				.withText("Reset")
				.withLayout(FluentButtonLayout.IMAGE_ON_SIDE)
				.withBordered(false)
				.withAction(() -> {
					heightSpinner.setValue(controller.rawDataController.getOriginalDataHeight());
					widthSpinner.setValue(controller.rawDataController.getOriginalDataWidth());
				});
	}
	
}
