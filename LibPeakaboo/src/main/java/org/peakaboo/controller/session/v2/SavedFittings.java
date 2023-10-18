package org.peakaboo.controller.session.v2;

import java.util.List;
import java.util.Map;

public class SavedFittings {
	
	public List<SavedFitting> fittings;
	public Map<String, String> annotations;
	public String solver; //FittingSolver
	public String fitter; //CurveFitter
	public String model; //FittingFunction
	public SavedFittingParameters calibration;
	
	public SavedFittings() {}
	
	public SavedFittings(
			List<SavedFitting> fittings,
			Map<String, String> annotations,
			String solver,
			String fitter,
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