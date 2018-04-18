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

	private FittingContext context;
	private double base;
	private double OneOverTwoSigmaSquared;
	private float mean, height;
	
	@Override
	public void initialize(FittingContext context) {
		this.context = context;
		float sigma = calcSigma();
		this.height = context.getHeight();
		this.mean = context.getEnergy();
		
		base = (
				
				               1.0f
				/*------------------------------*/ /
				   sigma*(Math.sqrt(2*Math.PI))
				
		);
		
		
		OneOverTwoSigmaSquared = 1d / (2 * sigma * sigma);
	}
	
	protected float calcSigma() {
		return context.getFWHM()/2.35482f;
	}
	
	public float forEnergy(float energy)
	{
		double exp = - (Math.pow((energy - mean), 2)  *  OneOverTwoSigmaSquared);
		
		return (float)(base * Math.exp(exp)) * height;
		
				
	}


	
}
