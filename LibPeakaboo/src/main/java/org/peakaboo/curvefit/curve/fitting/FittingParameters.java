package org.peakaboo.curvefit.curve.fitting;

import org.peakaboo.controller.session.v2.SavedFittingParameters;
import org.peakaboo.curvefit.peak.detector.DetectorMaterialType;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.curvefit.peak.fitting.FittingFunctionRegistry;
import org.peakaboo.framework.bolt.plugin.core.PluginDescriptor;

public class FittingParameters implements FittingParametersView {

	private FittingSet fits;
	
	private float fwhmBase = 0.080f;
	private EnergyCalibration calibration = new EnergyCalibration(0, 0, 0);
	private DetectorMaterialType detectorMaterial = DetectorMaterialType.SILICON;
	private PluginDescriptor<FittingFunction> fittingFunction = FittingFunctionRegistry.system().getPreset();
	private boolean showEscapePeaks = true;

	FittingParameters(FittingSet fits) {
		this.fits = fits;
	}

	/**
	 * Constructs a new FittingParameters object from the readonly parameters, but
	 * with a new {@link FittingSet} to work with
	 */
	public FittingParameters(FittingParametersView params, FittingSet fits) {
		this(params);
		this.fits = fits;
	}
	
	/**
	 * Constructs a new "dead" or "unwired" FittingParameters object without a
	 * reference to a parent {@link FittingSet} since a params view does not
	 * provide access to that.
	 */
	public FittingParameters(FittingParametersView params) {
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
		this((FittingParametersView)params);
		this.fits = params.fits;
	}
	
	@Override
	public FittingParameters copy() {
		return new FittingParameters(this);
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

	@Override
	public float getFWHMBase() {
		return fwhmBase;
	}
	
	public void setFWMHBase(float base) {
		this.fwhmBase = base;
		invalidate();
	}

	
	@Override
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
	
	@Override
	public DetectorMaterialType getDetectorMaterial() {
		return this.detectorMaterial;
	}

	public void setDetectorMaterial(DetectorMaterialType material) {
		this.detectorMaterial = material;
		invalidate();
	}
	
	public void setFittingFunction(PluginDescriptor<FittingFunction> proto) {
		this.fittingFunction = proto;
		invalidate();
	}

	@Override
	public PluginDescriptor<FittingFunction> getFittingFunction() {
		return fittingFunction;
	}

	@Override
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
