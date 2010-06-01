package peakaboo.curvefit.fitting;

/**
 * 
 * This turned out not to fit peaks well at all.
 * 
 * @author Nathaniel Sherry, 2009
 *
 */

public class LorentzFittingFunction implements FittingFunction{

	private double mean;
	private double gamma;
	private double height;
	
	public LorentzFittingFunction(double mean, double gamma, double height){
		
		this.mean = mean;
		this.gamma = gamma;
		this.height = height;
		
	}
	
	public double getHeightAtPoint(double point) {
		
		double value = 0.0;
		
		value = 
		(
			                         gamma
			/*----------------------------------------------------*/ / 
			   (Math.pow((point - mean), 2) + Math.pow(gamma, 2))
			
		) * (
			      1
			/*---------*/ /
			   Math.PI
		);
		
		
		
		return value * height;
	}

}
