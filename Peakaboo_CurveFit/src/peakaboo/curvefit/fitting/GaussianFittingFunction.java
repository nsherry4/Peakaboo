package peakaboo.curvefit.fitting;


/**
 * 
 * Gaussian functions seem to model XRF data very well.
 * 
 * @author Nathaniel Sherry, 2009
 *
 */

public class GaussianFittingFunction implements FittingFunction {

	public double sigma;
	public double mean;
	public double height;
	
	private double base;
	private double TwoSigmaSquared;
	
	
	
	public GaussianFittingFunction(double mean, double sigma, double height)
	{
		this.mean = mean;
		this.sigma = sigma;
		this.height = height;
		
		base = (
				
				               1.0
				/*------------------------------*/ /
				   sigma*(Math.sqrt(2*Math.PI))
				
		);
		
		
		TwoSigmaSquared = 2 * sigma * sigma;
	}
	
	public double getHeightAtPoint(double point)
	{
		double exp = - (
		
				   Math.pow((point - mean), 2)
				/*-----------------------------*/ /
						TwoSigmaSquared
		
		);
		
		return base * Math.exp(exp) * height;
		
				
	}
	

	
}
