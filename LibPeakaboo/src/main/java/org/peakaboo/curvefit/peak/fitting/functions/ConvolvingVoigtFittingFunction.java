package org.peakaboo.curvefit.peak.fitting.functions;

import org.peakaboo.curvefit.peak.fitting.TransitionFittingContext;
import org.peakaboo.curvefit.peak.fitting.DelegatingFittingContext;
import org.peakaboo.curvefit.peak.fitting.FittingContext;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;
import org.peakaboo.curvefit.peak.fitting.CustomFittingContext;
import org.peakaboo.curvefit.peak.transition.Transition;

public class ConvolvingVoigtFittingFunction implements FittingFunction {

	private FittingContext context;
	
	private FittingFunction signal;
	
	@Override
	public void initialize(FittingContext context) {
		this.context = context;
		signal = lorentz();
		FittingContext copy = new DelegatingFittingContext(context) {
			/*
			 * Hardcoded as 10ev because according to Handbook of X-Ray Spectrometry rev. 2
			 * p242, lorentz function represents actual xray emission, and is therefore on
			 * the order of 10ev wide. The Gaussian represents peak broadening from the
			 * detector and is on the order of 160 ev.
			 */
			@Override
			public float getFWHM() {
				return 0.010f;
			}
		};
		signal.initialize(copy);
	}

	@Override
	public float forEnergy(float energy) {
		return forEnergyAbsolute(energy) * context.getHeight();
	}
	
	@Override
	public float forEnergyAbsolute(float energy) {
		if(signal.forEnergyAbsolute(energy) < 0.0001) {
			return 0;
		}
		//Create a kernel transition+fittingfunction centered around the current energy
		FittingContext copy = new CustomFittingContext(context.getFittingParameters(), energy);
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
		
		if (normalizer == 0) {
			sum = 0;
		} else {
			sum /= normalizer;
		}
		
		return sum;
	}
	
	private GaussianFittingFunction gaussian() {
		return new GaussianFittingFunction();
	}
	
	private LorentzFittingFunction lorentz() {
		return new LorentzFittingFunction();
	}
	
	
	@Override
	public String name() {
		return "Convolving Voigt";
	}

	@Override
	public String toString() {
		return name() + " (Beta)";
	}
	
	@Override
	public String description() {
		return "Convolution of Gaussian and Lorentz functions, accurate but slow";
	}
	
	
}
