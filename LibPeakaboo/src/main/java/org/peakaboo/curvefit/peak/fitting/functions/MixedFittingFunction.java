package org.peakaboo.curvefit.peak.fitting.functions;

import org.peakaboo.curvefit.peak.fitting.FittingContext;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;

public class MixedFittingFunction implements FittingFunction {

	private FittingFunction f1, f2;
	private FittingContext context;
	private float percentF1;
	private float percentF2;
	
	public MixedFittingFunction(FittingFunction f1, FittingFunction f2, float percentF1, FittingContext context) {
		this.f1 = f1;
		this.f2 = f2;
		this.percentF1 = percentF1;
		this.percentF2 = 1f - percentF1;
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
		return (this.f1.forEnergyAbsolute(energy) * percentF1) + (this.f2.forEnergyAbsolute(energy) * percentF2);
	}
	
	@Override
	public String name() {
		return "Mixed (Do not use directly)";
	}

	@Override
	public String toString() {
		return name();
	}
	
	@Override
	public String description() {
		return "Weighted average of " + f1.name() + " and " + f2.name() + " functions";
	}
	
	
}
