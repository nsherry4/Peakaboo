package peakaboo.curvefit.fitting.functions;

import peakaboo.curvefit.fitting.context.FittingContext;
import peakaboo.curvefit.transition.Transition;
import peakaboo.curvefit.transition.TransitionSeriesType;

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

	
}
