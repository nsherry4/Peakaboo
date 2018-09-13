package peakaboo.mapping.calibration;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import peakaboo.curvefit.peak.transition.TransitionSeries;

public class CalibrationReference {

	private String name;
	private Map<TransitionSeries, Float> concentrations;
	
	public CalibrationReference() {
		name = "Empty Calibration Reference";
		concentrations = new HashMap<>();
	}
	
	public CalibrationReference(Path referenceFile) {
		concentrations = new HashMap<>();
		//TODO: Read some kind of reference file
	}

	public String getName() {
		return name;
	}

	public Map<TransitionSeries, Float> getConcentrations() {
		return new HashMap<>(concentrations);
	}
	
	
	
}
