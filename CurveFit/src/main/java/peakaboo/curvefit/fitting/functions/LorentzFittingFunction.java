package peakaboo.curvefit.fitting.functions;

import peakaboo.curvefit.fitting.context.FittingContext;

/**
 * 
 * This turned out not to fit peaks well at all.
 * 
 * @author Nathaniel Sherry, 2009
 *
 */

public class LorentzFittingFunction implements FittingFunction {

	private FittingContext context;
	private float gamma;
	private float gammaSquared;
	private float mean;
	private double OneOverPi = 1f / Math.PI;
	
	public void initialize(FittingContext context) {
		this.context = context;
		this.gamma = context.getFWHM()/2f;
		this.gammaSquared = gamma*gamma;
		this.mean = context.getEnergy();
	}

	public float forEnergy(float point) {
		
		double value = 0.0;
		
		value = 
		(
			                         gamma
			/*----------------------------------------------------*/ / 
			      (Math.pow((point - mean), 2) + gammaSquared)
			
		) * (
			OneOverPi
		);
		
		
		
		return (float)(value * context.getHeight());
	}

}
