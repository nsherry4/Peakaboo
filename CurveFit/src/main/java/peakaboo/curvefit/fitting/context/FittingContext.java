package peakaboo.curvefit.fitting.context;

import peakaboo.curvefit.fitting.FittingParameters;
import peakaboo.curvefit.peaktable.Element;
import peakaboo.curvefit.transition.EscapePeakType;
import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transition.TransitionSeriesType;

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
	
	
	public float getFWHM() {
		return parameters.getFWHM(transition);
	}
	
}
