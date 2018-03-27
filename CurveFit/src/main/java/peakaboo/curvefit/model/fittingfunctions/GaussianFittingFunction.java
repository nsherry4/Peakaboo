package peakaboo.curvefit.model.fittingfunctions;


/**
 * 
 * Gaussian functions seem to model XRF data very well.
 * 
 * @author Nathaniel Sherry, 2009
 *
 */

class GaussianFittingFunction implements FittingFunction {

	public float mean;
	public float height;
	
	private double base;
	private double TwoSigmaSquared;
	
	
	
	public GaussianFittingFunction(float mean, float sigma, float height)
	{
		
		this.mean = mean;
		this.height = height;
		
		base = (
				
				               1.0f
				/*------------------------------*/ /
				   sigma*(Math.sqrt(2*Math.PI))
				
		);
		
		
		TwoSigmaSquared = 2 * sigma * sigma;
	}
	
	public float getHeightAtPoint(float point)
	{
		double exp = - (
		
				   Math.pow((point - mean), 2)
				/*-----------------------------*/ /
						TwoSigmaSquared
		
		);
		
		return (float)(base * Math.exp(exp) * height);
		
				
	}
	

	
}
