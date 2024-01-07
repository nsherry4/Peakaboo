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
	public String pluginName() {
		return "Pseudo-Voigt";
	}

	@Override
	public String toString() {
		return pluginName();
	}

	@Override
	public String pluginDescription() {
		return "Sum of Gaussian and Lorentz functions approximating a Voigt function";
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "0cecebeb-30ff-4374-8c3a-c091669a83a6";
	}
	
}
