package org.peakaboo.curvefit.curve.fitting;

import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.controller.session.v2.SavedFittingParameters;
import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.fitting.EscapeFittingContext;
import org.peakaboo.curvefit.peak.fitting.FittingContext;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.curvefit.peak.fitting.TransitionFittingContext;
import org.peakaboo.curvefit.peak.fitting.functions.PseudoVoigtFittingFunction;
import org.peakaboo.curvefit.peak.table.Element;
import org.peakaboo.curvefit.peak.transition.Transition;

public class FittingParameters implements ROFittingParameters {

	private FittingSet fits;
	
	private float fwhmBase = 0.080f;
	private EnergyCalibration calibration = new EnergyCalibration(0, 0, 0);
	private DetectorMaterialType detectorMaterial = DetectorMaterialType.SILICON;
	private Class<? extends FittingFunction> fittingFunction = PseudoVoigtFittingFunction.class;
	private boolean showEscapePeaks = true;

	FittingParameters(FittingSet fits) {
		this.fits = fits;
	}

	/**
	 * Constructs a new FittingParameters object from the readonly parameters, but
	 * with a new {@link FittingSet} to work with
	 */
	public FittingParameters(ROFittingParameters params, FittingSet fits) {
		this(params);
		this.fits = fits;
	}
	
	/**
	 * Constructs a new "dead" or "unwired" FittingParameters object without a
	 * reference to a parent {@link FittingSet} since a readonly params does not
	 * provide access to that.
	 */
	public FittingParameters(ROFittingParameters params) {
		fwhmBase = params.getFWHMBase();
		calibration = params.getCalibration();
		detectorMaterial = params.getDetectorMaterial();
		fittingFunction = params.getFittingFunction();
		showEscapePeaks = params.getShowEscapePeaks();	
	}
	
	/**
	 * Complete copy of the given FittingParameters object
	 */
	public FittingParameters(FittingParameters params) {
		this((ROFittingParameters)params);
		this.fits = params.fits;
	}
	
	public FittingParameters copy() {
		return new FittingParameters(this);
	}
	
	
	public FittingFunction forTransition(Transition transition) {
		FittingContext context = new TransitionFittingContext(this, transition);
		return buildFunction(context);
	}

	public FittingFunction forEscape(Transition transition, Transition escape, Element element) {
		FittingContext context = new EscapeFittingContext(this, transition, escape, element);
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
	
	
	
	@Override
	public float getFWHM(float energy) {
		//See Handbook of X-Ray Spectrometry rev2 p282
		
		if (energy < 0) { energy = 0; }
		
		//Energy required to create electron-hole pair in detector material
		float energyGap = getDetectorMaterial().get().energyGap();
		float fano = getDetectorMaterial().get().fanoFactor();
		
		float noise = fwhmBase;
		
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
	
	public DetectorMaterialType getDetectorMaterial() {
		return this.detectorMaterial;
	}

	public void setDetectorMaterial(DetectorMaterialType material) {
		this.detectorMaterial = material;
		invalidate();
	}
	
	public void setFittingFunction(Class<? extends FittingFunction> cls) {
		this.fittingFunction = cls;
		invalidate();
	}

	public Class<? extends FittingFunction> getFittingFunction() {
		return fittingFunction;
	}

	public boolean getShowEscapePeaks() {
		return showEscapePeaks;
	}

	public void setShowEscapePeaks(boolean showEscapePeaks) {
		this.showEscapePeaks = showEscapePeaks;
		invalidate();
	}

	public SavedFittingParameters save() {
		return new SavedFittingParameters(
				calibration.getMinEnergy(), 
				calibration.getMaxEnergy(), 
				detectorMaterial.name(), 
				fwhmBase, 
				showEscapePeaks
			);
	}

}
