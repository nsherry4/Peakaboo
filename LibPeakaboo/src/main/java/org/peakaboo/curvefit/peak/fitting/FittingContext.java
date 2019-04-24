package org.peakaboo.curvefit.peak.fitting;

import org.peakaboo.curvefit.curve.fitting.FittingParameters;
import org.peakaboo.curvefit.peak.detector.DetectorMaterial;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.Transition;
import org.peakaboo.curvefit.peak.transition.TransitionShell;

public class FittingContext {

	private FittingParameters parameters;
	private float height;
	private float energy;
	private TransitionShell type;
	private Transition transition;
	
	public FittingContext(FittingParameters parameters, Transition transition, TransitionShell type) {
		this.height = transition.relativeIntensity;
		this.energy = transition.energyValue;
		this.transition = transition;
		this.type = type;
		this.parameters = parameters; 
	}
	
	//escape peak
	public FittingContext(FittingParameters parameters, Transition transition, Transition escape, Element element, TransitionShell type) {
		this.energy = transition.energyValue - escape.energyValue;
		this.height = transition.relativeIntensity * escape.relativeIntensity * DetectorMaterial.intensity(element);
		this.transition = transition;
		this.type = type;
		this.parameters = parameters;
	}

	public FittingContext(FittingContext copy) {
		this.parameters = copy.parameters;
		this.height = copy.height;
		this.energy = copy.energy;
		this.type = copy.type;
		this.transition = copy.transition;
	}
	
	public float getHeight() {
		return height;
	}
	
	public float getEnergy() {
		return energy;
	}

	public FittingParameters getFittingParameters() {
		return parameters;
	}
	
	public Transition getTransition() {
		return transition;
	}
	
	
	public float getFWHM() {
		return parameters.getFWHM(transition);
	}

	public TransitionShell getTransitionSeriesType() {
		return type;
	}
	
}
