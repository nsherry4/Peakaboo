package peakaboo.curvefit.fitting;

import peakaboo.curvefit.fitting.context.FittingContext;
import peakaboo.curvefit.fitting.context.StandardFittingContext;
import peakaboo.curvefit.fitting.functions.FittingFunction;
import peakaboo.curvefit.fitting.functions.PseudoVoigtFittingFunction;
import peakaboo.curvefit.peaktable.Element;
import peakaboo.curvefit.transition.EscapePeakType;
import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transition.TransitionSeriesType;

public class FittingParameters {

	private FittingSet fits;
	
	private float fwhmBase = 0.12245064f;
	private float fwhmMult = 0.00470964f;
	private	EnergyCalibration	calibration = new EnergyCalibration(0, 0, 0);
	private EscapePeakType		escapeType = EscapePeakType.NONE;
	
	private FittingParameters() {}
	
	FittingParameters(FittingSet fits) {
		this.fits = fits;
	}
	
	//Copy is used to give a "dead" copy of the parameters to a FittingResultSet. 
	//As such, it should not be given a copy of the FittingSet.
	public static FittingParameters copy(FittingParameters copyFrom) {
		FittingParameters param = new FittingParameters();
		param.fwhmBase = copyFrom.fwhmBase;
		param.fwhmMult = copyFrom.fwhmMult;
		param.fits = null;
		return param;
	}
	
	
	public FittingFunction forTransition(Transition transition, TransitionSeriesType type) {
		FittingContext context = new StandardFittingContext(this, transition, type);
		return buildFunction(context);
	}

	public FittingFunction forEscape(Transition transition, Transition escape, Element element, TransitionSeriesType type) {
		FittingContext context = new StandardFittingContext(this, transition, escape, element, type);
		return buildFunction(context);
	}

	private FittingFunction buildFunction(FittingContext context) {
		//TODO: This should be parameterized instead of being hard-coded
		FittingFunction function = new PseudoVoigtFittingFunction();
		function.initialize(context);
		return function;
	}
	
	private void invalidate() {
		if (fits != null) {
			this.fits.invalidateCurves();
		}
	}
	
	
	
	/**
	 * The FWHM value for a {@link Transition} changes based on the energy level. This method 
	 * calculates the FWHM value which should be used for this Transition.
	 */
	public float getFWHM(Transition t) {
		//y = mx+b
		return (fwhmMult * t.energyValue) + fwhmBase;
	}

	public float getFWHMBase() {
		return fwhmBase;
	}
	
	public void setFWMHBase(float base) {
		this.fwhmBase = base;
		invalidate();
	}

	
	public float getFWHMMult() {
		return fwhmBase;
	}
	
	public void setFWMHMult(float mult) {
		this.fwhmMult = mult;
		invalidate();
	}
	
	public EnergyCalibration getCalibration() {
		return calibration;
	}
	
	public void setCalibration(float minEnergy, float maxEnergy, int dataWidth) {
		setCalibration(new EnergyCalibration(minEnergy, maxEnergy, dataWidth));
	}
	
	public void setCalibration(EnergyCalibration calibration) {
		this.calibration = calibration;
		invalidate();
	}
	
	public EscapePeakType getEscapeType() {
		return this.escapeType;
	}

	public void setEscapeType(EscapePeakType escape) {
		this.escapeType = escape;
		invalidate();
	}

}
