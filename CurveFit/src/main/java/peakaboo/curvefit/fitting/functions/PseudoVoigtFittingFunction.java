package peakaboo.curvefit.fitting.functions;

import peakaboo.curvefit.fitting.context.FittingContext;

public class PseudoVoigtFittingFunction implements FittingFunction {

	private FittingFunction backer;
	
	@Override
	public void initialize(FittingContext context) {
		FittingFunction g = new GaussianFittingFunction();
		g.initialize(context);
		
		FittingFunction l = new LorentzFittingFunction();
		l.initialize(context);
		
		this.backer = new MixedFittingFunction(g, l, 0.8f);
	}
	
	@Override
	public float forEnergy(float energy) {
		return backer.forEnergy(energy);
	}

}
