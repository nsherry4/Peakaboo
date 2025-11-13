package org.peakaboo.tier;

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
import org.peakaboo.framework.bolt.plugin.core.ExtensionPointRegistry;
import org.peakaboo.framework.bolt.repository.AggregatePluginRepository;
import org.peakaboo.framework.bolt.repository.BuiltinPluginRepository;
import org.peakaboo.framework.bolt.repository.HttpsPluginRepository;
import org.peakaboo.framework.bolt.repository.IssuePluginRepository;
import org.peakaboo.framework.bolt.repository.ManualInstallPluginRepository;
import org.peakaboo.mapping.filter.model.MapFilterRegistry;

public class BasicTierProvider implements TierProvider {
	
	private ExtensionPointRegistry extensionPoints;
	private AggregatePluginRepository pluginRepositories;
	
	@Override
	public CalibrationController createPlotCalibrationController(PlotController plotController) {
		return new BasicCalibrationController();
	}
	
	@Override
	public void initializePlugins() {
		extensionPoints = new ExtensionPointRegistry();
		extensionPoints.addRegistry(DataSourceRegistry.system());
		extensionPoints.addRegistry(DataSinkRegistry.system());
		extensionPoints.addRegistry(FilterRegistry.system());
		extensionPoints.addRegistry(MapFilterRegistry.system());
		
		pluginRepositories = new AggregatePluginRepository(List.of(
				new HttpsPluginRepository("https://github.com/PeakabooLabs/PeakabooPlugins/releases/download/610/", 610),
				new BuiltinPluginRepository(DataSourceRegistry.system()),
				new BuiltinPluginRepository(DataSinkRegistry.system())
			));
		pluginRepositories.addRepository(new ManualInstallPluginRepository(extensionPoints, pluginRepositories::listAvailablePlugins));
		pluginRepositories.addRepository(new IssuePluginRepository(extensionPoints));
		
		
	}
	
	@Override
	public ExtensionPointRegistry getExtensionPoints() {
		return extensionPoints;
	}

	@Override
	public AggregatePluginRepository getPluginRepositories() {
		return pluginRepositories;
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
