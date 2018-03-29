package peakaboo.curvefit.fitting.functions;

import peakaboo.curvefit.fitting.context.FittingContext;
/**
 * 
 * Gaussian functions seem to model XRF data very well.
 * 
 * @author Nathaniel Sherry, 2009
 *
 */

public class GaussianFittingFunction implements FittingFunction {

	private double base;
	private double TwoSigmaSquared;
	private float mean, height;
	
	@Override
	public void initialize(FittingContext context) {
				
		float sigma = context.getFWHM()/2.35482f;
		this.height = context.getHeight();
		this.mean = context.getEnergy();
		
		base = (
				
				               1.0f
				/*------------------------------*/ /
				   sigma*(Math.sqrt(2*Math.PI))
				
		);
		
		
		TwoSigmaSquared = 2 * sigma * sigma;
	}
	
	public float forEnergy(float energy)
	{
		double exp = - (
		
				Math.pow((energy - mean), 2)
				/*------------------------*/ /
						TwoSigmaSquared
		
		);
		
		return (float)(base * Math.exp(exp)) * height;
		
				
	}


	
}
