package peakaboo.curvefit.fitting.functions;

import peakaboo.curvefit.fitting.context.FittingContext;

public class MixedFittingFunction implements FittingFunction {

	private FittingFunction f1, f2;
	private float percentF1;
	
	public MixedFittingFunction(FittingFunction f1, FittingFunction f2, float percentF1) {
		this.f1 = f1;
		this.f2 = f2;
		this.percentF1 = percentF1;
	}

	public void initialize(FittingContext context) {
		f1.initialize(context);
		f2.initialize(context);
	}
	
	@Override
	public float forEnergy(float energy) {
		float f1v = this.f1.forEnergy(energy) * percentF1;
		float f2v = this.f2.forEnergy(energy) * (1f - percentF1);
		return f1v + f2v;
	}
	
}
