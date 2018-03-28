package peakaboo.curvefit.model.fittingfunctions;

/**
 * 
 * This turned out not to fit peaks well at all.
 * 
 * @author Nathaniel Sherry, 2009
 *
 */

class LorentzFittingFunction extends SimpleFittingFunction {


	
	public LorentzFittingFunction(float mean, float fwhm, float height) {
		super(mean, fwhm/2f, height);
	}

	public float getHeightAtPoint(float point) {
		
		double value = 0.0;
		
		value = 
		(
			                         width
			/*----------------------------------------------------*/ / 
			   (Math.pow((point - mean), 2) + Math.pow(width, 2))
			
		) * (
			      1
			/*---------*/ /
			   Math.PI
		);
		
		
		
		return (float)(value * height);
	}

}
