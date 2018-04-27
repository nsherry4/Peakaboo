package peakaboo.curvefit.fitting.functions;

import peakaboo.curvefit.fitting.context.FittingContext;

public class PseudoVoigtFittingFunction implements FittingFunction {

	private FittingFunction backer;
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
		return forEnergyAbsolute(energy) * context.getHeight();
	}
	
	@Override
	public float forEnergyAbsolute(float energy) {
		return backer.forEnergyAbsolute(energy);
	}

}
