package org.peakaboo.curvefit.peak.fitting.functions;

import org.peakaboo.curvefit.peak.fitting.FittingContext;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;

public class PseudoVoigtFittingFunction implements FittingFunction {

	private MixedFittingFunction backer;
	private FittingContext context;
	
	@Override
	public void initialize(FittingContext context) {
		this.context = context;
		
		FittingFunction g = new GaussianFittingFunction();
		g.initialize(context);
		
		FittingFunction l = new LorentzFittingFunction();
		l.initialize(context);
		
		//Gaussian and Lorentz functions should have the same FWHM, which they will get from `context`
		this.backer = new MixedFittingFunction(g, l, 0.7f, context);
	}
	
	@Override
	public float forEnergy(float energy) {
		return backer.forEnergyAbsolute(energy) * context.getHeight();
	}
	
	@Override
	public float forEnergyAbsolute(float energy) {
		return backer.forEnergyAbsolute(energy);
	}

	@Override
	public String name() {
		return "Pseudo-Voigt";
	}

	@Override
	public String toString() {
		return name();
	}
	
}
