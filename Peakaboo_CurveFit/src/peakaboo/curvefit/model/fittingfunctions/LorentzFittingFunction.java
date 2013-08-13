package peakaboo.curvefit.model.fittingfunctions;

/**
 * 
 * This turned out not to fit peaks well at all.
 * 
 * @author Nathaniel Sherry, 2009
 *
 */

public class LorentzFittingFunction implements FittingFunction{

	private float mean;
	private float gamma;
	private float height;
	
	public LorentzFittingFunction(float mean, float gamma, float height){
		
		this.mean = mean;
		this.gamma = gamma;
		this.height = height;
		
	}
	
	public float getHeightAtPoint(float point) {
		
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
		
		
		
		return (float)(value * height);
	}

}
