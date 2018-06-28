package peakaboo.curvefit.peak.fitting;

import peakaboo.curvefit.curve.fitting.FittingParameters;
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.EscapePeakType;
import peakaboo.curvefit.peak.transition.Transition;
import peakaboo.curvefit.peak.transition.TransitionSeriesType;

public class FittingContext {

	private FittingParameters parameters;
	private float height;
	private float energy;
	private TransitionSeriesType type;
	private Transition transition;
	
	public FittingContext(FittingParameters parameters, Transition transition, TransitionSeriesType type) {
		this.height = transition.relativeIntensity;
		this.energy = transition.energyValue;
		this.transition = transition;
		this.type = type;
		this.parameters = parameters; 
	}
	
	//escape peak
	public FittingContext(FittingParameters parameters, Transition transition, Transition escape, Element element, TransitionSeriesType type) {
		this.energy = transition.energyValue - escape.energyValue;
		this.height = transition.relativeIntensity * escape.relativeIntensity * EscapePeakType.escapeIntensity(element);
		this.transition = transition;
		this.type = type;
		this.parameters = parameters;
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

	public TransitionSeriesType getTransitionSeriesType() {
		return type;
	}
	
}
