package peakaboo.ui.swing.plotting.toolbar;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import peakaboo.controller.plotter.PlotController;
import peakaboo.ui.swing.plotting.PlotPanel;
import swidget.icons.IconSize;
import swidget.widgets.ImageButton;
import swidget.widgets.ImageButton.ButtonSize;
import swidget.widgets.SettingsPanel;
import swidget.widgets.Spacing;
import swidget.widgets.ToolbarImageButton;

public class PlotMenuEnergy extends JPopupMenu {

	private PlotController controller;
	
	private JSpinner minEnergy, maxEnergy;
	private ImageButton energyGuess;
	
	public PlotMenuEnergy(PlotPanel plot, PlotController controller) {
		this.controller = controller;
		
		
		SettingsPanel energy = new SettingsPanel(Spacing.iTiny());
		energy.setOpaque(false);
		energy.setBorder(Spacing.bMedium());
		JLabel energyTitle = new JLabel("Energy Calibration (keV)");
		energyTitle.setHorizontalAlignment(SwingConstants.CENTER);
		energyTitle.setFont(energyTitle.getFont().deriveFont(Font.BOLD));
		energy.addSetting(energyTitle);
		
		
		
		minEnergy = new JSpinner();
		minEnergy.setModel(new SpinnerNumberModel(0.0, -20.48, 20.48, 0.01));
		minEnergy.getEditor().setPreferredSize(new Dimension(72, (int)minEnergy.getPreferredSize().getHeight()));
		minEnergy.getEditor().setOpaque(false);
		minEnergy.addChangeListener(e -> {
			float min = ((Number) minEnergy.getValue()).floatValue();
			if (min > controller.fitting().getMaxEnergy()) {
				min = controller.fitting().getMaxEnergy() - 0.01f;
				minEnergy.setValue(min);
			} 
			controller.fitting().setMinEnergy(min);	
		});
		energy.addSetting(minEnergy, "Minimum");
		
		
		maxEnergy = new JSpinner();
		maxEnergy.setModel(new SpinnerNumberModel(20.48, 0.0, 204.8, 0.01));
		maxEnergy.getEditor().setPreferredSize(new Dimension(72, (int)maxEnergy.getPreferredSize().getHeight()));
		maxEnergy.getEditor().setOpaque(false);
		maxEnergy.addChangeListener(e -> {
			float max = ((Number) maxEnergy.getValue()).floatValue();
			if (max < controller.fitting().getMinEnergy()) {
				max = controller.fitting().getMinEnergy() + 0.01f;
				maxEnergy.setValue(max);
			} 
			controller.fitting().setMaxEnergy(max);
		});
		energy.addSetting(maxEnergy, "Maximum");

		energyGuess = new ImageButton("Guess Calibration")
				.withIcon("auto", IconSize.TOOLBAR_SMALL)
				.withTooltip("Try to detect the correct max energy value by matching fittings to strong signal. Use with care.")
				.withBordered(false);
		energyGuess.addActionListener(e -> {
			//custom controls in a menu don't hide the menu when activated
			this.setVisible(false);
			plot.actionGuessMaxEnergy();	
		});
		energy.addSetting(energyGuess);
		
		
		
		
		
		SettingsPanel advanced = new SettingsPanel(Spacing.iTiny());
		advanced.setOpaque(false);
		advanced.setBorder(Spacing.bMedium());
		JButton advancedButton = new ImageButton("Advanced Options")
				.wittButtonSize(ButtonSize.COMPACT)
				.withAction(() -> {
					this.setVisible(false);
					plot.actionShowAdvancedOptions();		
				});

		advancedButton.setHorizontalAlignment(SwingConstants.CENTER);
		advancedButton.setFont(advancedButton.getFont().deriveFont(Font.BOLD));
		advanced.addSetting(advancedButton);


		
		
		SettingsPanel outer = new SettingsPanel(Spacing.iSmall());

		outer.addSetting(energy);
		outer.addSetting(advanced);
		outer.setOpaque(false);
		this.add(outer);
		
		
	}

	public void setWidgetState(boolean hasData) {
		minEnergy.setEnabled(hasData);
		maxEnergy.setEnabled(hasData);
		minEnergy.setValue((double) controller.fitting().getMinEnergy());
		maxEnergy.setValue((double) controller.fitting().getMaxEnergy());
		energyGuess.setEnabled(hasData);
	}
	
}
