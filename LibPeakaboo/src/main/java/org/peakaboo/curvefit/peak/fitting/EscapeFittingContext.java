package org.peakaboo.curvefit.peak.fitting;

import org.peakaboo.curvefit.curve.fitting.FittingParametersView;
import org.peakaboo.curvefit.peak.detector.DetectorMaterial;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.Transition;

public class EscapeFittingContext implements FittingContext {

	private FittingParametersView parameters;
	private float height;
	private float energy;
	
	public EscapeFittingContext(FittingParametersView parameters, Transition transition, Transition escape, Element element) {
		this.energy = transition.energyValue - escape.energyValue;
		this.height = transition.relativeIntensity * escape.relativeIntensity * DetectorMaterial.intensity(element);
		this.parameters = parameters;
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public float getEnergy() {
		return energy;
	}

	@Override
	public float getFWHM() {
		return parameters.getFWHM(getEnergy());
	}

	@Override
	public FittingParametersView getFittingParameters() {
		return parameters;
	}
	
}
