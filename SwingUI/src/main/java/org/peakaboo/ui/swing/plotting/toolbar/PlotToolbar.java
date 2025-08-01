package org.peakaboo.ui.swing.plotting.toolbar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Optional;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.framework.stratus.api.Stratus;
import org.peakaboo.framework.stratus.api.icons.IconSize;
import org.peakaboo.framework.stratus.api.icons.StockIcon;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButton.NotificationDotState;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentButtonConfig.BorderStyle;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.button.FluentToolbarButton;
import org.peakaboo.framework.stratus.components.ui.fluentcontrols.menu.FluentPopupAlignment;
import org.peakaboo.tier.Tier;
import org.peakaboo.tier.TierUIAction;
import org.peakaboo.ui.swing.app.PeakabooIcons;
import org.peakaboo.ui.swing.app.TierWidgetFactory;
import org.peakaboo.ui.swing.plotting.PlotPanel;
import org.peakaboo.ui.swing.plugins.browser.PluginRepositoryBrowser;

public class PlotToolbar extends JToolBar {

	private PlotController controller;
	private PlotPanel plot;
	
	private FluentToolbarButton exportMenuButton;
	private FluentToolbarButton saveButton;
	private FluentToolbarButton toolbarMap;
	private FluentToolbarButton toolbarInfo;
	private FluentToolbarButton energyMenuButton, viewMenuButton, settingsMenuButton, pluginsMenuButton;

	
	private PlotMenuEnergy energyMenu;
	private PlotMenuView viewMenu;
	private PlotMenuMain mainMenu;
	private PlotMenuExport exportMenu;
	
	public static final String TIER_LOCATION = "plot.toolbar";
	private final List<TierUIAction<PlotPanel, PlotController>> tierItems = Tier.provider().uiComponents(TIER_LOCATION);
	
	//===MAIN MENU WIDGETS===
	

	
	
	public PlotToolbar(PlotPanel plot, PlotController controller) {
		this.plot = plot;
		this.controller = controller;
		
		setFloatable(false);
		
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.insets = new Insets(2, 2, 2, 2);
		c.fill = GridBagConstraints.NONE;

		FluentToolbarButton ibutton = new FluentToolbarButton("Open", StockIcon.DOCUMENT_OPEN)
				.withTooltip("Open a new data set or session")
				.withAction(plot::actionOpenData);
		this.add(ibutton, c);


		c.gridx += 1;
		saveButton = new FluentToolbarButton("Save", StockIcon.DOCUMENT_SAVE)
				.withTooltip("Saves your session (eg: fittings but not dataset) to a file for later use")
				.withAction(plot::actionSaveSession);
		this.add(saveButton, c);
		
		c.gridx += 1;
		this.add(createExportMenuButton(), c);
		

		c.gridx += 1;
		this.add(new JToolBar.Separator( null ), c);

		
		
		
		toolbarInfo = new FluentToolbarButton("Scan Info", StockIcon.BADGE_INFO).withTooltip("Displays extended information about this data set");
		toolbarInfo.addActionListener(e -> plot.actionShowInfo());
		c.gridx += 1;
		toolbarInfo.setEnabled(false);
		this.add(toolbarInfo, c);
		
		toolbarMap = new FluentToolbarButton("Map Fittings")
				.withIcon(PeakabooIcons.MAP, IconSize.TOOLBAR_SMALL)
				.withTooltip("Display a 2D map of the relative intensities of the fitted elements")
				.withSignificance(true).withAction(plot::actionMap);
		
		
		c.gridx += 1;
		toolbarMap.setEnabled(false);
		this.add(toolbarMap, c);

		
		
		for (var item : tierItems) {
			var tierButton = TierWidgetFactory.toolbarButton(item, plot, controller);
			item.component = tierButton;
			c.gridx += 1;
			this.add(tierButton, c);
			tierButton.setEnabled(false);
			
		}
		
		

		c.gridx += 1;
		c.weightx = 1.0;
		this.add(Box.createHorizontalGlue(), c);
		c.weightx = 0.0;


		c.gridx++;
		energyMenuButton = createEnergyMenuButton();
		this.add(energyMenuButton, c);
				
		c.gridx++;
		viewMenuButton = createViewMenuButton();
		this.add(viewMenuButton, c);
		
		c.gridx++;
		settingsMenuButton = createSettingsMenuButton();
		this.add(settingsMenuButton, c);
		
		c.gridx++;
		pluginsMenuButton = createPluginsMenuButton();
		this.add(pluginsMenuButton, c);
		
		c.gridx++;
		this.add(createMainMenuButton(), c);
		
	}
	


	public void setWidgetState(boolean hasData) {
		
		toolbarInfo.setEnabled(hasData);
		
		for (TierUIAction<PlotPanel, PlotController> item : tierItems) {
			JComponent component = (JComponent) item.component;
			boolean enabled = item.enabled.apply(controller);
			component.setEnabled(enabled);
		}

		if (hasData) {
			toolbarMap.setEnabled(controller.fitting().canMap());
		}
		
		
		exportMenuButton.setEnabled(hasData);
		saveButton.setEnabled(hasData && plot.hasUnsavedWork());
		
		energyMenu.setWidgetState(hasData);
		viewMenu.setWidgetState(hasData);
		mainMenu.setWidgetState(hasData);
		exportMenu.setWidgetState(hasData);
		
		energyMenuButton.setEnabled(hasData);
		viewMenuButton.setEnabled(hasData);
		
		boolean hasEnergyCalibration = !controller.fitting().getEnergyCalibration().isZero();
		if (hasData && hasEnergyCalibration || !hasData) {
			energyMenuButton.withBordered(BorderStyle.ACTIVE);
			energyMenuButton.withNotificationDot(Optional.empty());
		} else {
			energyMenuButton.withBordered(BorderStyle.ALWAYS);
			energyMenuButton.withNotificationDot(NotificationDotState.PROBLEM);
		}
		
		
		var pluginsRepo = Tier.provider().getPluginRepositories();
		boolean hasPluginNotifications = pluginsRepo.listAvailablePlugins()
				.stream()
				.map(PluginRepositoryBrowser::hasNotification)
				.anyMatch(b -> b);
		
		if (hasPluginNotifications) {
			pluginsMenuButton.withNotificationDot(NotificationDotState.EVENT);
			pluginsMenuButton.withBordered(BorderStyle.ALWAYS);
		} else {
			pluginsMenuButton.withNotificationDot(NotificationDotState.OFF);
			pluginsMenuButton.withBordered(BorderStyle.ACTIVE);
		}
		
		
	}
	
	private FluentToolbarButton createExportMenuButton() {
		exportMenu = new PlotMenuExport(plot);
		exportMenuButton = new FluentToolbarButton()
				.withIcon(StockIcon.DOCUMENT_EXPORT)
				.withTooltip("Export Data")
				.withPopupMenuAction(exportMenu);
		return exportMenuButton;
	}

	private FluentToolbarButton createEnergyMenuButton() {
		energyMenu = new PlotMenuEnergy(plot, controller);
		var colour = Stratus.getTheme().getControlText();
		return new FluentToolbarButton()
				.withIcon(PeakabooIcons.MENU_ENERGY, colour)
				.withTooltip("Energy & Peak Calibration")
				.withPopupMenuAction(energyMenu, FluentPopupAlignment.CENTER);
	}
	
	private FluentToolbarButton createMainMenuButton() {
		mainMenu = new PlotMenuMain(plot, controller);
		var colour = Stratus.getTheme().getControlText();
		return new FluentToolbarButton()
				.withIcon(PeakabooIcons.MENU_MAIN, colour)
				.withTooltip("Main Menu")
				.withPopupMenuAction(mainMenu, true);
	}

	private FluentToolbarButton createViewMenuButton() {
		viewMenu = new PlotMenuView(plot, controller);
		var colour = Stratus.getTheme().getControlText();
		return new FluentToolbarButton()
				.withIcon(PeakabooIcons.MENU_VIEW, colour)
				.withTooltip("Plot Display Options")
				.withPopupMenuAction(viewMenu, FluentPopupAlignment.CENTER);
	}
	
	
	private FluentToolbarButton createSettingsMenuButton() {
		var colour = Stratus.getTheme().getControlText();
		return new FluentToolbarButton()
				.withIcon(PeakabooIcons.MENU_SETTINGS, colour)
				.withTooltip("Advanced Settings & Tuneables")
				.withAction(() -> plot.actionShowAdvancedOptions());
	}
	
	
	private FluentToolbarButton createPluginsMenuButton() {
		var colour = Stratus.getTheme().getControlText();
		return new FluentToolbarButton()
				.withIcon(PeakabooIcons.MENU_PLUGIN, colour)
				.withTooltip("Plugins")
				.withAction(() -> plot.actionShowPlugins());
	}
	

	
	
}
