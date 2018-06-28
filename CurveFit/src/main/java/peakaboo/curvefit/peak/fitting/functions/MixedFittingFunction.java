package peakaboo.curvefit.peak.fitting.functions;

import peakaboo.curvefit.peak.fitting.FittingContext;
import peakaboo.curvefit.peak.fitting.FittingFunction;

public class MixedFittingFunction implements FittingFunction {

	private FittingFunction f1, f2;
	private FittingContext context;
	private float percentF1;
	
	
	public MixedFittingFunction(FittingFunction f1, FittingFunction f2, float percentF1, FittingContext context) {
		this.f1 = f1;
		this.f2 = f2;
		this.percentF1 = percentF1;
		this.context = context;
	}

	public void initialize(FittingContext context) {
		f1.initialize(context);
		f2.initialize(context);
	}
	
	@Override
	public float forEnergy(float energy) {
		return forEnergyAbsolute(energy) * context.getHeight();
	}
	
	@Override
	public float forEnergyAbsolute(float energy) {
		float f1v = this.f1.forEnergyAbsolute(energy) * percentF1;
		float f2v = this.f2.forEnergyAbsolute(energy) * (1f - percentF1);
		return f1v + f2v;
	}
	
}
