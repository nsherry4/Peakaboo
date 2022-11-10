package org.peakaboo.tier;

import java.util.List;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.calibration.CalibrationController;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;

//Interface for a factory class that creates pro/non-pro implementations of interfaces

public interface TierProvider {
	
	public CalibrationController createPlotCalibrationController(PlotController plotController);
	public CalibrationProfile createCalibrationProfile();
	
	public void initializePlugins();
	
	public List<BoltPluginManager<? extends BoltPlugin>> getPluginManagers();

	public <V, C> List<TierUIItem<V, C>> uiComponents(String location);

	public String appName();
	public String tierName();
	
	public String assetPath();
	
}
