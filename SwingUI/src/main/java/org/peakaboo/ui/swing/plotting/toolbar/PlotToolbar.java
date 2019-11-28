package org.peakaboo.ui.swing.plotting.toolbar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JToolBar;

import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.framework.swidget.icons.IconSize;
import org.peakaboo.framework.swidget.icons.StockIcon;
import org.peakaboo.framework.swidget.widgets.fluent.button.FluentToolbarButton;
import org.peakaboo.ui.swing.Peakaboo;
import org.peakaboo.ui.swing.plotting.PlotPanel;

public class PlotToolbar extends JToolBar {

	private PlotController controller;
	private PlotPanel plot;
	
	private FluentToolbarButton exportMenuButton;
	private FluentToolbarButton saveButton;
	private FluentToolbarButton toolbarMap;
	private FluentToolbarButton toolbarConcentrations;
	private FluentToolbarButton toolbarInfo;

	
	private PlotMenuEnergy energyMenu;
	private PlotMenuView viewMenu;
	private PlotMenuMain mainMenu;
	private PlotMenuExport exportMenu;
	
	 
	
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

		FluentToolbarButton ibutton = new FluentToolbarButton("Open", "document-open").withTooltip("Open a new data set or session");
		ibutton.addActionListener(e -> plot.actionOpenData());
		this.add(ibutton, c);

		// controls.add(button);



		c.gridx += 1;
		saveButton = new FluentToolbarButton("Save", StockIcon.DOCUMENT_SAVE_AS).withTooltip("Saves your session (eg: fittings but not dataset) to a file for later use");
		saveButton.withAction(plot::actionSaveSession);
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
	
		if (Peakaboo.SHOW_QUANTITATIVE) {
			toolbarConcentrations = new FluentToolbarButton("Concentration")
					.withIcon("calibration", IconSize.TOOLBAR_SMALL)
					.withTooltip("Display concentration estimates for the fitted elements. Requires a Z-Calibration Profile.")
					.withSignificance(false)
					.withAction(plot::actionShowConcentrations);
			
			c.gridx += 1;
			toolbarConcentrations.setEnabled(false);
			this.add(toolbarConcentrations, c);
		}
		
		toolbarMap = new FluentToolbarButton("Map Fittings")
				.withIcon("map", IconSize.TOOLBAR_SMALL)
				.withTooltip("Display a 2D map of the relative intensities of the fitted elements")
				.withSignificance(true).withAction(plot::actionMap);
		
		c.gridx += 1;
		toolbarMap.setEnabled(false);
		this.add(toolbarMap, c);


		c.gridx += 1;
		c.weightx = 1.0;
		this.add(Box.createHorizontalGlue(), c);
		c.weightx = 0.0;


		c.gridx++;
		this.add(createEnergyMenuButton(), c);
				
		c.gridx++;
		this.add(createViewMenuButton(), c);
		
		c.gridx++;
		this.add(createMainMenuButton(), c);
		
	}
	
	public void setWidgetState(boolean hasData) {
		
		toolbarInfo.setEnabled(hasData);
		if (Peakaboo.SHOW_QUANTITATIVE) toolbarConcentrations.setEnabled(hasData && controller.calibration().hasCalibrationProfile() && controller.fitting().canMap()); 
		
		if (hasData) {
			toolbarMap.setEnabled(controller.fitting().canMap());
		}
		
		
		exportMenuButton.setEnabled(hasData);
		saveButton.setEnabled(hasData && plot.hasUnsavedWork());
		
		energyMenu.setWidgetState(hasData);
		viewMenu.setWidgetState(hasData);
		mainMenu.setWidgetState(hasData);
		exportMenu.setWidgetState(hasData);
		
		
	}
	
	private FluentToolbarButton createExportMenuButton() {
		exportMenuButton = new FluentToolbarButton().withIcon(StockIcon.DOCUMENT_EXPORT).withTooltip("Export Data");
		exportMenu = new PlotMenuExport(plot);
		exportMenuButton.addActionListener(e -> exportMenu.show(exportMenuButton, 0, exportMenuButton.getHeight()));
		return exportMenuButton;
	}

	private FluentToolbarButton createEnergyMenuButton() {
		FluentToolbarButton menuButton = new FluentToolbarButton().withIcon("menu-energy").withTooltip("Energy & Peak Calibration");
		energyMenu = new PlotMenuEnergy(plot, controller);
		menuButton.addActionListener(e -> energyMenu.show(menuButton, (int)(menuButton.getWidth() - energyMenu.getPreferredSize().getWidth()), menuButton.getHeight()));
		return menuButton;
	}
	
	private FluentToolbarButton createMainMenuButton() {
		FluentToolbarButton menuButton = new FluentToolbarButton(StockIcon.MENU_MAIN).withTooltip("Main Menu");
		mainMenu = new PlotMenuMain(plot, controller);
		menuButton.addActionListener(e -> mainMenu.show(menuButton, (int)(menuButton.getWidth() - mainMenu.getPreferredSize().getWidth()), menuButton.getHeight()));
		return menuButton;
	}

	private FluentToolbarButton createViewMenuButton() {
		FluentToolbarButton menuButton = new FluentToolbarButton().withIcon("menu-view").withTooltip("Plot Settings Menu");
		viewMenu = new PlotMenuView(plot, controller);
		menuButton.addActionListener(e -> viewMenu.show(menuButton, (int)(menuButton.getWidth() - viewMenu.getPreferredSize().getWidth()), menuButton.getHeight()));
		return menuButton;
	}
	
	

	

	
	
}
