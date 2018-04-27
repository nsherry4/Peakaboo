package peakaboo.curvefit.fitting.functions;

import peakaboo.curvefit.fitting.context.FittingContext;

public class IdaFittingFunction implements FittingFunction {

	private float mean, gamma, height;
	
	public void initialize(FittingContext context) {
		this.mean = context.getEnergy();
		this.gamma = context.getFWHM()/1.762747174f;
		this.height = context.getHeight();
	}

	@Override
	public float forEnergy(float energy) {
		return forEnergyAbsolute(energy) * height;
	}
	
	@Override
	public float forEnergyAbsolute(float energy) {
		float x = energy - mean;
		return (float)Math.pow(sech(x/gamma), 2);
	}

	private float sech(float value) {
		float temp = (float) Math.exp(value);
		return 2 / (temp + 1 / temp);
	}
	
}
