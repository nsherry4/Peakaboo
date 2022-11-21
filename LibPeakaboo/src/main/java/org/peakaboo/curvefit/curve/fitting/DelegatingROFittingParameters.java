package org.peakaboo.curvefit.curve.fitting;

import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.Transition;

public class DelegatingROFittingParameters implements ROFittingParameters {

	private ROFittingParameters params;
	
	public DelegatingROFittingParameters(ROFittingParameters params) {
		this.params = params;
	}

	@Override
	public ROFittingParameters copy() {
		return new DelegatingROFittingParameters(params.copy());
	}

	@Override
	public FittingFunction forTransition(Transition transition) {
		return params.forTransition(transition);
	}

	@Override
	public FittingFunction forEscape(Transition transition, Transition escape, Element element) {
		return params.forEscape(transition, escape, element);
	}

	@Override
	public float getFWHM(float energy) {
		return params.getFWHM(energy);
	}

	@Override
	public float getFWHMBase() {
		return params.getFWHMBase();
	}

	@Override
	public EnergyCalibration getCalibration() {
		return params.getCalibration();
	}

	@Override
	public DetectorMaterialType getDetectorMaterial() {
		return params.getDetectorMaterial();
	}

	@Override
	public Class<? extends FittingFunction> getFittingFunction() {
		return params.getFittingFunction();
	}

	@Override
	public boolean getShowEscapePeaks() {
		return params.getShowEscapePeaks();
	}

	public String toString() {
		return params.toString();
	}
	
	
	
}
