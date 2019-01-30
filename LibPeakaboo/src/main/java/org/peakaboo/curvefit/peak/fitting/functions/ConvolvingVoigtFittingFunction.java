package org.peakaboo.curvefit.peak.fitting.functions;

import org.peakaboo.curvefit.peak.fitting.FittingContext;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.curvefit.peak.transition.Transition;

public class ConvolvingVoigtFittingFunction implements FittingFunction {

	private FittingContext context;
	
	private FittingFunction signal;
	
	@Override
	public void initialize(FittingContext context) {
		this.context = context;
		signal = lorentz();
		signal.initialize(context);
	}

	@Override
	public float forEnergy(float energy) {
		return forEnergyAbsolute(energy) * context.getHeight();
	}
	
	@Override
	public float forEnergyAbsolute(float energy) {
		if(signal.forEnergyAbsolute(energy) < 0.0001) {
			return 0;
		} else {
		}
		//Create a kernel transition+fittingfunction centered around the current energy
		Transition fake = new Transition(energy, 1f, "Fake Transition for Voigt Fitting Function");
		FittingContext copy = new FittingContext(context.getFittingParameters(), fake, context.getTransitionSeriesType());
		FittingFunction kernel = gaussian();
		kernel.initialize(copy);
		
		return convolveForEnergy(energy, kernel);

	}
	
	/**
	 * Solves a function convolution at the given energy 
	 */
	private float convolveForEnergy(float energy, FittingFunction kernel) {
		float max = kernel.forEnergyAbsolute(energy);
		float deltaEnergy = 0f;
		float sum = 0f;
		float kernelValue, signalValue;
		float normalizer = 0;
		while (true) {
			//kernel is centered at `energy`, so we can take advantage of 
			//symmetry to only call it once for +/-
			kernelValue = kernel.forEnergyAbsolute(energy + deltaEnergy);
			signalValue = signal.forEnergyAbsolute(energy + deltaEnergy) + signal.forEnergyAbsolute(energy - deltaEnergy);
			
			if (kernelValue < 0.001f * max && kernelValue < 0.1f && signalValue < 0.2f) break;
			
			sum += kernelValue * signalValue; //same as kernel * signal1 + kernel * signal2
			normalizer += kernelValue + kernelValue;
			
			deltaEnergy += 0.002;
		}
		
		sum /= normalizer;
		
		return sum;
	}
	
	private GaussianFittingFunction gaussian() {
		return new GaussianFittingFunction();
	}
	
	private LorentzFittingFunction lorentz() {
		return new LorentzFittingFunction( ) {
			protected float calcGamma() {
				/*
				 * /16 because according to Handbook of X-Ray Spectrometry rev. 2 p242, 
				 * lorentz function represents actual xray emission, and is therefore 
				 * on the order of 10ev wide. The Gaussian represents peak broadening
				 * from the detector and is on the order of 160 ev. Since the fitting
				 * function FWHM is calibrated based on observed peak shape, we have
				 * to calibrate the lorenzian on that basis.
				 */
				return super.calcGamma() / 16f;
			}
		};
	}
	
	
	@Override
	public String name() {
		return "Convolving Voigt";
	}

	@Override
	public String toString() {
		return name() + " (Beta)";
	}
	
	
}
