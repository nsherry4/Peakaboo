package org.peakaboo.tier;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.calibration.BasicDetectorProfile;
import org.peakaboo.calibration.DetectorProfile;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.calibration.BasicCalibrationController;
import org.peakaboo.controller.plotter.calibration.CalibrationController;
import org.peakaboo.dataset.sink.plugin.DataSinkRegistry;
import org.peakaboo.dataset.source.model.components.scandata.analysis.Analysis;
import org.peakaboo.dataset.source.model.components.scandata.analysis.DataSourceAnalysis;
import org.peakaboo.dataset.source.plugin.DataSourceRegistry;
import org.peakaboo.display.plot.Plotter;
import org.peakaboo.filter.model.FilterRegistry;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginRegistry;
import org.peakaboo.framework.bolt.repository.HttpsPluginRepository;
import org.peakaboo.framework.bolt.repository.LocalPluginRepository;
import org.peakaboo.framework.bolt.repository.PluginRepository;
import org.peakaboo.mapping.filter.model.MapFilterRegistry;

public class BasicTierProvider implements TierProvider {
	
	@Override
	public CalibrationController createPlotCalibrationController(PlotController plotController) {
		return new BasicCalibrationController();
	}
	
	public void initializePlugins() {
		//nothing to do
	}
	
	public List<BoltPluginRegistry<? extends BoltPlugin>> getPluginManagers() {
		return List.of(
			DataSourceRegistry.system(),
			DataSinkRegistry.system(),
			FilterRegistry.system(),
			MapFilterRegistry.system()
		);
	}

	public List<PluginRepository> getPluginRepositories() {
		return List.of(
				new HttpsPluginRepository("https://github.com/PeakabooLabs/peakaboo-plugins/releases/download/600/")
				//new LocalPluginRepository(DataSourceRegistry.system()),
				//new LocalPluginRepository(DataSinkRegistry.system())
			);
	}
	
	@Override
	public <V, C> List<TierUIAction<V, C>> uiComponents(String location) {
		return List.of();
	}

	@Override
	public DetectorProfile createDetectorProfile() {
		return new BasicDetectorProfile();
	}

	@Override
	public String appName() {
		return "Peakaboo";
	}

	@Override
	public String tierName() {
		return "XRF Analysis";
	}

	@Override
	public String assetPath() {
		return "/org/peakaboo/ui/swing/";
	}

	@Override
	public List<TierUIAutoGroup<PlotController>> getAdvancedOptions() {
		return List.of();
	}

	@Override
	public DataSourceAnalysis createDataSourceAnalysis(List<Analysis> analyses) {
		return DataSourceAnalysis.merge(analyses);
	}

	@Override
	public DataSourceAnalysis createDataSourceAnalysis() {
		return new DataSourceAnalysis();
	}

	@Override
	public Plotter createPlotter() {
		return new Plotter();
	}
	
	

}
