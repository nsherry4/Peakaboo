package peakaboo.curvefit.fitting.context;

import peakaboo.curvefit.fitting.FittingParameters;
import peakaboo.curvefit.peaktable.Element;
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
		this.height = transition.relativeIntensity * escape.relativeIntensity * escapeIntensity(element);
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


	private float escapeIntensity(Element e)
	{
		/*
		 * The paper
		 * 
		 * " Measurement and calculation of escape peak intensities in synchrotron radiation X-ray fluorescence analysis
		 * S.X. Kang a, X. Sun a, X. Ju b, Y.Y. Huang b, K. Yao a, Z.Q. Wu a, D.C. Xian b"
		 * 
		 * provides a listing of escape peak intensities relative to the real peak by element. By taking this data into
		 * openoffice and fitting an exponential regression line to it, we arrive at the formula esc(z) = (543268.59
		 * z^-4.48)%
		 */

		return 543268.59f * (float) Math.pow((e.ordinal() + 1), -4.48) / 100.0f;
	}

	
}
