package org.peakaboo.tier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.peakaboo.calibration.BasicCalibrationProfile;
import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.controller.plotter.PlotController;
import org.peakaboo.controller.plotter.calibration.BasicCalibrationController;
import org.peakaboo.controller.plotter.calibration.CalibrationController;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;
import org.peakaboo.framework.bolt.plugin.core.BoltPluginManager;

public class BasicTierProvider implements TierProvider {
	
	@Override
	public CalibrationController createPlotCalibrationController(PlotController plotController) {
		return new BasicCalibrationController();
	}
	
	public void initializePlugins() {
		//nothing to do
	}
	
	public List<BoltPluginManager<? extends BoltPlugin>> getPluginManagers() {
		return new ArrayList<>();
	}

	@Override
	public <V, C> List<TierUIAction<V, C>> uiComponents(String location) {
		return List.of();
	}

	@Override
	public CalibrationProfile createCalibrationProfile() {
		return new BasicCalibrationProfile();
	}

	@Override
	public String appName() {
		return "Peakaboo";
	}

	@Override
	public String tierName() {
		return "Community Edition";
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
	public Collection<? extends FittingFunction> getFittingFunctions() {
		return List.of();
	}
	
	

}
