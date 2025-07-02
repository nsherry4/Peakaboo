package org.peakaboo.tier;

import java.util.List;

import org.peakaboo.calibration.DetectorProfile;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.calibration.CalibrationController;
import org.peakaboo.dataset.source.model.components.scandata.analysis.Analysis;
import org.peakaboo.dataset.source.model.components.scandata.analysis.DataSourceAnalysis;
import org.peakaboo.display.plot.Plotter;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.repository.PluginRepository;

//Interface for a factory class that creates pro/non-pro implementations of interfaces

public interface TierProvider {
	
	//Calibration
	public CalibrationController createPlotCalibrationController(PlotController plotController);
	public DetectorProfile createDetectorProfile();
	
	// Plugins
	public void initializePlugins();
	public List<BoltPluginRegistry<? extends BoltPlugin>> getPluginManagers();
	public <V, C> List<TierUIAction<V, C>> uiComponents(String location);
	public List<PluginRepository> getPluginRepositories();

	// App info
	public String appName();
	public String tierName();
	
	
	// App asset paths
	public String assetPath();
	default String iconPath() {
		return this.assetPath() + "/icons/";
	}
	
	// Extra options for fitting/solving
	public List<TierUIAutoGroup<PlotController>> getAdvancedOptions();

	
	// Data source analysis implementation
	public DataSourceAnalysis createDataSourceAnalysis(List<Analysis> analyses);
	public DataSourceAnalysis createDataSourceAnalysis();
	
	// Drawing overrides
	public Plotter createPlotter();
	
}
