package org.peakaboo.controller.session.v2;

import java.util.List;
import java.util.Map;

import org.peakaboo.framework.bolt.plugin.java.SavedPlugin;

public class SavedFittings {
	
	public List<SavedFitting> fittings;
	public Map<String, String> annotations;
	public SavedPlugin solver; //FittingSolver
	public SavedPlugin fitter; //CurveFitter
	public String model; //FittingFunction
	public SavedFittingParameters calibration;
	
	public SavedFittings() {}
	
	public SavedFittings(
			List<SavedFitting> fittings,
			Map<String, String> annotations,
			SavedPlugin solver,
			SavedPlugin fitter,
			String model,
			SavedFittingParameters calibration
		) {
		this.fittings = fittings;
		this.annotations = annotations;
		this.solver = solver;
		this.fitter = fitter;
		this.model = model;
		this.calibration = calibration;
	}

}