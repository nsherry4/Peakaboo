package org.peakaboo.curvefit.peak.fitting.functions;

import org.peakaboo.curvefit.peak.fitting.FittingContext;
import org.peakaboo.curvefit.peak.fitting.FittingFunction;

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
	private double oneOverTwoSigmaSquared;
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
		
		
		oneOverTwoSigmaSquared = 1d / (2 * sigma * sigma);
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
		double exp = - (Math.pow((energy - mean), 2)  *  oneOverTwoSigmaSquared);
		return (float)(base * Math.exp(exp));
	}


	@Override
	public String pluginName() {
		return "Gaussian";
	}

	@Override
	public String toString() {
		return pluginName();
	}
	
	@Override
	public String pluginDescription() {
		return "Gaussian (normal) distribution function";
	}

	@Override
	public String pluginVersion() {
		return "1.0";
	}

	@Override
	public String pluginUUID() {
		return "56e042ea-e006-4228-8e4d-22f7cf1930ea";
	}
	
	
}
