package peakaboo.curvefit.curve.fitting;

import java.util.logging.Level;

import peakaboo.common.PeakabooLog;
import peakaboo.curvefit.peak.escape.EscapePeakType;
import peakaboo.curvefit.peak.fitting.FittingContext;
import peakaboo.curvefit.peak.fitting.FittingFunction;
import peakaboo.curvefit.peak.fitting.functions.PseudoVoigtFittingFunction;
import peakaboo.curvefit.peak.table.Element;
import peakaboo.curvefit.peak.transition.Transition;
import peakaboo.curvefit.peak.transition.TransitionShell;

public class FittingParameters {

	private FittingSet fits;
	
	private float fwhmBase = 0.080f;
	private	EnergyCalibration	calibration = new EnergyCalibration(0, 0, 0);
	private EscapePeakType		escapeType = EscapePeakType.NONE;
	private Class<? extends FittingFunction> fittingFunction = PseudoVoigtFittingFunction.class;
	
	private FittingParameters() {}
	
	FittingParameters(FittingSet fits) {
		this.fits = fits;
	}
	
	//Copy is used to give a "dead" copy of the parameters to a FittingResultSet. 
	//As such, it should not be given a copy of the FittingSet.
	public static FittingParameters copy(FittingParameters copyFrom) {
		FittingParameters param = new FittingParameters();
		param.fwhmBase = copyFrom.fwhmBase;
		param.fits = null;
		param.escapeType = copyFrom.escapeType;
		param.fittingFunction = copyFrom.fittingFunction;
		//immutable
		param.calibration = copyFrom.calibration;
		return param;
	}
	
	public FittingParameters copy() {
		return FittingParameters.copy(this);
	}
	
	
	public FittingFunction forTransition(Transition transition, TransitionShell type) {
		FittingContext context = new FittingContext(this, transition, type);
		return buildFunction(context);
	}

	public FittingFunction forEscape(Transition transition, Transition escape, Element element, TransitionShell type) {
		FittingContext context = new FittingContext(this, transition, escape, element, type);
		return buildFunction(context);
	}

	private FittingFunction buildFunction(FittingContext context) {
		FittingFunction function;
		try {
			function = fittingFunction.newInstance();
			function.initialize(context);
			return function;
		} catch (InstantiationException | IllegalAccessException e) {
			PeakabooLog.get().log(Level.SEVERE, "Failed to create fitting function, using default", e);
			return new PseudoVoigtFittingFunction();
		}
		
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
		//See Handbook of X-Ray Spectrometry rev2 p282
		
		//Energy required to create electron-hole pair in detector material
		float energyGap = getEscapeType().get().energyGap();
		float fano = getEscapeType().get().fanoFactor();
		
		float noise = fwhmBase;
		
		float energy = t.energyValue;
		float noiseComponent = (float) (Math.pow(noise / 2.3548, 2));
		float energyComponent = (float) (energyGap*fano*energy);
			
		float sigmaSquared = noiseComponent + energyComponent;
		float sigma = (float) Math.sqrt(sigmaSquared);
		
		//gaussian sigma to ev
		float fwhm = sigma * 2.35482f;
		
		return fwhm;
		
	}

	public float getFWHMBase() {
		return fwhmBase;
	}
	
	public void setFWMHBase(float base) {
		this.fwhmBase = base;
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
	
	public void setFittingFunction(Class<? extends FittingFunction> cls) {
		this.fittingFunction = cls;
		invalidate();
	}

	public Class<? extends FittingFunction> getFittingFunction() {
		return fittingFunction;
	}

}
