package peakaboo.curvefit.model.fittingfunctions;

/**
 * 
 * Defines a general interface for a function to return values at discreet points
 * 
 * @author Nathaniel Sherry, 2009
 *
 */

public interface FittingFunction {

	
	/**
	 * Gets the value for this function at the specified point.
	 * @param point the point to evauluate this function at
	 * @return the result of evaluating this function at the given point
	 */
	float getHeightAtPoint(float point);
	
}
