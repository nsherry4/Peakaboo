package peakaboo.curvefit.model.fittingfunctions;


/**
 * 
 * Gaussian functions seem to model XRF data very well.
 * 
 * @author Nathaniel Sherry, 2009
 *
 */

class GaussianFittingFunction extends SimpleFittingFunction {

	private double base;
	private double TwoSigmaSquared;
	
	
	
	public GaussianFittingFunction(float mean, float fwhm, float height)
	{
		super(mean, fwhm/2.35482f, height);
		
		base = (
				
				               1.0f
				/*------------------------------*/ /
				   width*(Math.sqrt(2*Math.PI))
				
		);
		
		
		TwoSigmaSquared = 2 * width * width;
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
