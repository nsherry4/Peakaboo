package org.peakaboo.ui.swing.plotting.toolbar;

import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.widgets.Spacing;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButton;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentButtonSize;
import org.peakaboo.framework.swidget.widgets.layout.SettingsPanel;
import org.peakaboo.tier.Tier;
import org.peakaboo.tier.TierUIAction;
import org.peakaboo.ui.swing.environment.PeakabooIcons;
import org.peakaboo.ui.swing.plotting.PlotPanel;

public class PlotMenuEnergy extends JPopupMenu {

	private PlotController controller;
	
	private JSpinner minEnergy, maxEnergy, noiseEnergy;
	private FluentButton energyGuess;
	
	public static final String TIER_LOCATION = "plot.energymenu";
	private final List<TierUIAction<PlotPanel, PlotController>> tierItems = Tier.provider().uiComponents(TIER_LOCATION);
	
	public PlotMenuEnergy(PlotPanel plot, PlotController controller) {
		this.controller = controller;
		
		
		SettingsPanel outer = new SettingsPanel(Spacing.iSmall());

		outer.addSetting(energyCalibration(plot));
		
		boolean hasTierComponents = false;
		SettingsPanel tierui = new SettingsPanel();
		tierui.setOpaque(false);
		tierui.setBorder(Spacing.bMedium());
		for (TierUIAction<PlotPanel, PlotController> item : tierItems) {
			hasTierComponents = true;
			JButton button = new FluentButton(item.text)
					.withButtonSize(FluentButtonSize.COMPACT)
					.withAction(() -> {
						this.setVisible(false);
						item.action.accept(plot, controller);	
					});
			button.setHorizontalAlignment(SwingConstants.CENTER);
			button.setFont(button.getFont().deriveFont(Font.BOLD));
			
			tierui.addSetting(button);
			item.component = button;
		}
		
		if (hasTierComponents) {
			outer.addSetting(tierui);
		}
		
		outer.setOpaque(false);
		this.add(outer);
		
		
	}

	

	
	private SettingsPanel energyCalibration(PlotPanel plot) {

		SettingsPanel energy = new SettingsPanel(Spacing.iTiny());
		energy.setOpaque(false);
		energy.setBorder(Spacing.bMedium());
		JLabel energyTitle = new JLabel("Energy Calibration");
		energyTitle.setHorizontalAlignment(SwingConstants.CENTER);
		energyTitle.setFont(energyTitle.getFont().deriveFont(Font.BOLD));
		energyTitle.setBorder(new EmptyBorder(0, 0, Spacing.small, 0));
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
		minEnergy.setToolTipText("Energy value of lowest channel in spectrum");
		energy.addSetting(minEnergy, "Min (keV)");
		
		
		maxEnergy = new JSpinner();
		maxEnergy.setModel(new SpinnerNumberModel(20.48, 0.0, 2040.8, 0.01));
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
		maxEnergy.setToolTipText("Energy value of highest channel in spectrum");
		energy.addSetting(maxEnergy, "Max (keV)");

		
		noiseEnergy = new JSpinner();
		noiseEnergy.setModel(new SpinnerNumberModel(controller.fitting().getFWHMBase()*1000, 0.0, 1000.0, 0.1));
		noiseEnergy.getEditor().setPreferredSize(new Dimension(72, (int)noiseEnergy.getPreferredSize().getHeight()));
		noiseEnergy.getEditor().setOpaque(false);
		noiseEnergy.addChangeListener(e -> {
			
			float base = ((Number) noiseEnergy.getValue()).floatValue()/1000;
			controller.fitting().setFWHMBase(base);
			
		});
		noiseEnergy.setToolTipText("FWHM of gaussian detector noise peak contribution");
		energy.addSetting(noiseEnergy, "Noise (eV)");
		
		energyGuess = new FluentButton("Guess Calibration")
				.withIcon(PeakabooIcons.AUTO, IconSize.TOOLBAR_SMALL)
				.withTooltip("Try to detect the correct max energy value by matching fittings to strong signal. Use with care.")
				.withBordered(false)
				.withAction(() -> {
					//custom controls in a menu don't hide the menu when activated
					this.setVisible(false);
					plot.actionGuessMaxEnergy();	
				});
		energy.addSetting(energyGuess);
		
		return energy;
	}

	public void setWidgetState(boolean hasData) {
		
		
		maxEnergy.setEnabled(hasData);
		float modelMax = controller.fitting().getMaxEnergy();
		float viewMax = ((Number) maxEnergy.getValue()).floatValue();
		if (modelMax != viewMax) {
			maxEnergy.setValue((double) modelMax);
		}
		

		minEnergy.setEnabled(hasData);
		float modelMin = controller.fitting().getMinEnergy();
		float viewMin = ((Number) minEnergy.getValue()).floatValue();
		if (modelMin != viewMin) {
			minEnergy.setValue((double) modelMin);
		}

		
		energyGuess.setEnabled(hasData);
	}
	
}
