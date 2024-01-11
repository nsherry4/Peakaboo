package org.peakaboo.curvefit.peak.fitting;

import org.peakaboo.curvefit.curve.fitting.FittingParametersView;
import org.peakaboo.curvefit.peak.transition.Transition;

public class TransitionFittingContext implements FittingContext {

	private FittingParametersView parameters;
	private Transition transition;
	
	public TransitionFittingContext(FittingParametersView parameters, Transition transition) {
		this.transition = transition;
		this.parameters = parameters; 
	}

	public TransitionFittingContext(TransitionFittingContext copy) {
		this.parameters = copy.parameters;
		this.transition = copy.transition;
	}
	
	@Override
	public float getHeight() {
		return transition.relativeIntensity;
	}
	
	@Override
	public float getEnergy() {
		return transition.energyValue;
	}

	@Override
	public FittingParametersView getFittingParameters() {
		return parameters;
	}
			
	@Override
	public float getFWHM() {
		return parameters.getFWHM(getEnergy());
	}
	
}
