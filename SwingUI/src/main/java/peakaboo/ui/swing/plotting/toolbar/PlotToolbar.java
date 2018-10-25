package peakaboo.ui.swing.plotting.toolbar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToolBar;

import peakaboo.controller.plotter.PlotController;
import peakaboo.ui.swing.plotting.PlotPanel;
import swidget.icons.IconSize;
import swidget.icons.StockIcon;
import swidget.widgets.buttons.ImageButton;
import swidget.widgets.buttons.ToolbarImageButton;

public class PlotToolbar extends JToolBar {

	private PlotController controller;
	private PlotPanel plot;
	
	private ToolbarImageButton toolbarSnapshot;
	private ToolbarImageButton toolbarMap;
	private ToolbarImageButton toolbarConcentrations;
	private ToolbarImageButton toolbarInfo;

	
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

		ToolbarImageButton ibutton = new ToolbarImageButton("Open", "document-open").withTooltip("Open a new data set");
		ibutton.addActionListener(e -> plot.actionOpenData());
		this.add(ibutton, c);

		// controls.add(button);

		toolbarSnapshot = new ToolbarImageButton("Save Image", StockIcon.DEVICE_CAMERA).withTooltip("Save a picture of the current plot");
		toolbarSnapshot.addActionListener(e -> plot.actionSavePicture());
		c.gridx += 1;
		this.add(toolbarSnapshot, c);

		toolbarInfo = new ToolbarImageButton("Scan Info", StockIcon.BADGE_INFO).withTooltip("Displays extended information about this data set");
		toolbarInfo.addActionListener(e -> plot.actionShowInfo());
		c.gridx += 1;
		toolbarInfo.setEnabled(false);
		this.add(toolbarInfo, c);
	
		
		toolbarMap = new ToolbarImageButton("Map Fittings")
				.withIcon("map", IconSize.TOOLBAR_SMALL)
				.withTooltip("Display a 2D map of the relative intensities of the fitted elements")
				.withSignificance(true);
		toolbarMap.addActionListener(e -> plot.actionMap());
		
		c.gridx += 1;
		toolbarMap.setEnabled(false);
		this.add(toolbarMap, c);

		
		toolbarConcentrations = new ToolbarImageButton("Concentrations")
				.withIcon("calibration", IconSize.TOOLBAR_SMALL)
				.withTooltip("Display concentration estimates for the fitted elements. Requires a Z-Calibration Profile.")
				.withSignificance(true);
		toolbarConcentrations.addActionListener(e -> plot.actionShowConcentrations());
		
		c.gridx += 1;
		toolbarConcentrations.setEnabled(false);
		this.add(toolbarConcentrations, c);

		
		
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
		toolbarConcentrations.setEnabled(hasData && controller.calibration().hasCalibrationProfile() && controller.fitting().canMap()); 
		
		if (hasData) {
			toolbarMap.setEnabled(controller.fitting().canMap() && controller.data().getDataSet().getDataSource().isContiguous());
		}
		
		
		energyMenu.setWidgetState(hasData);
		viewMenu.setWidgetState(hasData);
		mainMenu.setWidgetState(hasData);
		
		
	}
	

	private ToolbarImageButton createEnergyMenuButton() {
		ToolbarImageButton menuButton = new ToolbarImageButton().withIcon("menu-energy").withTooltip("Energy & Peak Calibration");
		energyMenu = new PlotMenuEnergy(plot, controller);
		menuButton.addActionListener(e -> energyMenu.show(menuButton, (int)(menuButton.getWidth() - energyMenu.getPreferredSize().getWidth()), menuButton.getHeight()));
		return menuButton;
	}
	
	private ToolbarImageButton createMainMenuButton() {
		ToolbarImageButton menuButton = new ToolbarImageButton(StockIcon.MENU_MAIN).withTooltip("Main Menu");
		mainMenu = new PlotMenuMain(plot, controller);
		menuButton.addActionListener(e -> mainMenu.show(menuButton, (int)(menuButton.getWidth() - mainMenu.getPreferredSize().getWidth()), menuButton.getHeight()));
		return menuButton;
	}

	private ToolbarImageButton createViewMenuButton() {
		ToolbarImageButton menuButton = new ToolbarImageButton().withIcon("menu-view").withTooltip("Plot Settings Menu");
		viewMenu = new PlotMenuView(plot, controller);
		menuButton.addActionListener(e -> viewMenu.show(menuButton, (int)(menuButton.getWidth() - viewMenu.getPreferredSize().getWidth()), menuButton.getHeight()));
		return menuButton;
	}
	
	

	

	
	
}
