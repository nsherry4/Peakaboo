package peakaboo.ui.swing.plotting.toolbar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JToolBar;

import peakaboo.controller.plotter.PlotController;
import peakaboo.ui.swing.plotting.PlotPanel;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.ImageButton;
import swidget.widgets.ToolbarImageButton;

public class PlotToolbar extends JToolBar {

	private PlotController controller;
	private PlotPanel plot;
	
	private ImageButton toolbarSnapshot;
	private ImageButton toolbarMap;
	private ImageButton toolbarInfo;

	
	private PlotMenuEnergy energyMenu;
	private PlotMenuView viewMenu;
	private PlotMenuMain mainMenu;
	
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

		ImageButton ibutton = new ToolbarImageButton("document-open", "Open", "Open a new data set");
		ibutton.addActionListener(e -> plot.actionOpenData());
		this.add(ibutton, c);

		// controls.add(button);

		toolbarSnapshot = new ToolbarImageButton(
			StockIcon.DEVICE_CAMERA,
			"Save Image",
			"Save a picture of the current plot");
		toolbarSnapshot.addActionListener(e -> plot.actionSavePicture());
		c.gridx += 1;
		this.add(toolbarSnapshot, c);

		toolbarInfo = new ToolbarImageButton(
			StockIcon.BADGE_INFO,
			"Scan Info",
			"Displays extended information about this data set");
		toolbarInfo.addActionListener(e -> plot.actionShowInfo());
		c.gridx += 1;
		toolbarInfo.setEnabled(false);
		this.add(toolbarInfo, c);
	
		
		toolbarMap = new ImageButton("Map Fittings")
				.withIcon("map", IconSize.TOOLBAR_SMALL)
				.withTooltip("Display a 2D map of the relative intensities of the fitted elements")
				.withLayout(ToolbarImageButton.significantLayout);
		toolbarMap.addActionListener(e -> plot.actionMap());
		
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
		
		toolbarSnapshot.setEnabled(hasData);
		toolbarInfo.setEnabled(hasData);
		
		if (hasData) {
			toolbarMap.setEnabled(controller.fitting().canMap() && controller.data().getDataSet().getDataSource().isContiguous());
		}
		
		
		energyMenu.setWidgetState(hasData);
		viewMenu.setWidgetState(hasData);
		mainMenu.setWidgetState(hasData);
		
		
	}
	

	private ToolbarImageButton createEnergyMenuButton() {
		ToolbarImageButton menuButton = new ToolbarImageButton("menu-energy", "Energy & Peak Calibration");
		energyMenu = new PlotMenuEnergy(plot, controller);
		menuButton.addActionListener(e -> energyMenu.show(menuButton, (int)(menuButton.getWidth() - energyMenu.getPreferredSize().getWidth()), menuButton.getHeight()));
		return menuButton;
	}
	
	private ToolbarImageButton createMainMenuButton() {
		ToolbarImageButton menuButton = new ToolbarImageButton(StockIcon.MENU_MAIN, "Main Menu");
		mainMenu = new PlotMenuMain(plot, controller);
		menuButton.addActionListener(e -> mainMenu.show(menuButton, (int)(menuButton.getWidth() - mainMenu.getPreferredSize().getWidth()), menuButton.getHeight()));
		return menuButton;
	}

	private ToolbarImageButton createViewMenuButton() {
		ToolbarImageButton menuButton = new ToolbarImageButton("menu-view", "Plot Settings Menu");
		viewMenu = new PlotMenuView(plot, controller);
		menuButton.addActionListener(e -> viewMenu.show(menuButton, (int)(menuButton.getWidth() - viewMenu.getPreferredSize().getWidth()), menuButton.getHeight()));
		return menuButton;
	}
	
	

	

	
	
}
