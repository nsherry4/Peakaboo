package peakaboo.curvefit.peak.fitting;

/**
 * 
 * Defines a general interface for a function to return values at discreet points
 * 
 * @author Nathaniel Sherry, 2009
 *
 */

public interface FittingFunction {

	
	/**
	 * Allows us to have a zero-arg constructor
	 * @param context
	 */
	void initialize(FittingContext context);
	
	/**
	 * Gets the value for this function at the specified point.
	 * @param energy the energy to evauluate this function at
	 * @return the result of evaluating this function at the given point
	 */
	float forEnergy(float energy);

	/**
	 * Similar to forEnergy, but the value returned is not scaled
	 * to the relative intensity of the transition it represents.
	 */
	float forEnergyAbsolute(float energy);
	
}
