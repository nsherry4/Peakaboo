package peakaboo.curvefit.peak.fitting.functions;

import peakaboo.curvefit.peak.fitting.FittingContext;
import peakaboo.curvefit.peak.fitting.FittingFunction;

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
	
	@Override
	public float forEnergy(float energy)
	{
		return forEnergyAbsolute(energy) * height;
	}
	
	public float forEnergyAbsolute(float energy) {
		double exp = - (Math.pow((energy - mean), 2)  *  OneOverTwoSigmaSquared);
		return (float)(base * Math.exp(exp));
	}


	
}
