package org.peakaboo.curvefit.peak.fitting.functions;

import org.peakaboo.curvefit.peak.fitting.FittingContext;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;

/**
 * 
 * This turned out not to fit peaks well at all.
 * 
 * @author Nathaniel Sherry, 2009
 *
 */

public class LorentzFittingFunction implements FittingFunction {

	protected FittingContext context;
	private float gamma;
	private float gammaSquared;
	private float mean;
	private double OneOverPiGamma;
	
	public void initialize(FittingContext context) {
		this.context = context;
		this.gamma = calcGamma();
		this.gammaSquared = gamma*gamma;
		this.OneOverPiGamma = 1f / (Math.PI * gamma);
		
		this.mean = context.getEnergy();
	}
	
	protected float calcGamma() {
		return context.getFWHM()/2f;
	}
	
	
	public float getGamma() {
		return gamma;
	}

	@Override
	public float forEnergy(float energy) {
		return forEnergyAbsolute(energy) * context.getHeight();
	}
	
	public float forEnergyAbsolute(float energy) {
		
		double value = 
		(
				                 gammaSquared
			/*----------------------------------------------------*/ / 
			      (Math.pow((energy - mean), 2) + gammaSquared)
			
		) * (
			OneOverPiGamma
		);
		
		
		return (float)value;
	}
	
	@Override
	public String name() {
		return "Lorentz";
	}

	@Override
	public String toString() {
		return name();
	}
	

}
