package peakaboo.curvefit.fitting.context;

import peakaboo.curvefit.fitting.FittingParameters;
import peakaboo.curvefit.peaktable.Element;
import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transitionseries.TransitionSeriesType;

public class StandardFittingContext implements FittingContext {

	private FittingParameters parameters;
	private float height;
	private float energy;
	private TransitionSeriesType type;

	public StandardFittingContext(FittingParameters parameters, Transition transition, TransitionSeriesType type) {
		this.height = transition.relativeIntensity;
		this.energy = transition.energyValue;
		this.type = type;
		this.parameters = parameters; 
	}
	
	//escape peak
	public StandardFittingContext(FittingParameters parameters, Transition transition, Transition escape, Element element, TransitionSeriesType type) {
		this.energy = transition.energyValue - escape.energyValue;
		this.height = transition.relativeIntensity * escape.relativeIntensity * escapeIntensity(element);
		this.type = type;
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

	public FittingParameters getFittingParameters() {
		return parameters;
	}
	
	


	@Override
	//TODO: In the future, this can draw on the fitting parameters to customize this
	public float getFWHM() {
		//float sigma = (0.062f - 0.01f) + (energy / 500.0f);
		//float fwhm = sigma * 2.35482f;
		
		//return 0.1225f + (energy / 212.0f);
		return 0.11f + (energy / 212.0f);
		
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
