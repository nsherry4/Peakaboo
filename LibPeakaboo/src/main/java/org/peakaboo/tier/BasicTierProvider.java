package org.peakaboo.tier;

import java.io.File;
import java.util.List;

import org.peakaboo.app.Version;
import org.peakaboo.calibration.BasicDetectorProfile;
import org.peakaboo.calibration.DetectorProfile;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.calibration.BasicCalibrationController;
import org.peakaboo.controller.plotter.calibration.CalibrationController;
import org.peakaboo.controller.plotter.view.mode.ChannelViewModeRegistry;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterRegistry;
import org.peakaboo.curvefit.curve.fitting.solver.FittingSolverRegistry;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
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
import org.peakaboo.framework.bolt.repository.PluginMetadata;
import org.peakaboo.framework.bolt.repository.PluginRepository;
import org.peakaboo.mapping.filter.model.MapFilterRegistry;

public class BasicTierProvider implements TierProvider {
	
	private ExtensionPointRegistry extensionPoints;
	private AggregatePluginRepository pluginRepositories;
	
	@Override
	public CalibrationController createPlotCalibrationController(PlotController plotController) {
		return new BasicCalibrationController();
	}
	
	@Override
	public void initializePlugins(File pluginsRoot) {
		CurveFitterRegistry.init();
		FittingSolverRegistry.init();
		ChannelViewModeRegistry.init();
		FittingFunctionRegistry.init();

		DataSourceRegistry.init(pluginsRoot);
		DataSinkRegistry.init(pluginsRoot);
		FilterRegistry.init(pluginsRoot);
		MapFilterRegistry.init(pluginsRoot);

		extensionPoints = new ExtensionPointRegistry();
		extensionPoints.addRegistry(DataSourceRegistry.system());
		extensionPoints.addRegistry(DataSinkRegistry.system());
		extensionPoints.addRegistry(FilterRegistry.system());
		extensionPoints.addRegistry(MapFilterRegistry.system());
		
		String infix = Version.RELEASE_TYPE == Version.ReleaseType.RELEASE ? "" : "Testing";
		List<PluginRepository> knownRepositories = List.of(
				new HttpsPluginRepository("https://github.com/PeakabooLabs/PeakabooPlugins" + infix + "/releases/download/v6.2/", 620),
				new BuiltinPluginRepository(DataSourceRegistry.system()),
				new BuiltinPluginRepository(DataSinkRegistry.system())
			);
		
		pluginRepositories = new AggregatePluginRepository(knownRepositories);
		pluginRepositories.addRepository(new ManualInstallPluginRepository(extensionPoints, this::knownInventory));
		pluginRepositories.addRepository(new IssuePluginRepository(extensionPoints));
		
		
	}
	
	/**
	 * Get a listing of plugins excluding manual installs. Needed by manual install "repo" for
	 * making sure there is no repo match for a local plugin
	 */
	private List<PluginMetadata> knownInventory() {
		var others = pluginRepositories.getRepositories().stream()
				.filter(repo -> !(repo instanceof ManualInstallPluginRepository))
				.toList();
		return new AggregatePluginRepository(others).listAvailablePlugins();
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
