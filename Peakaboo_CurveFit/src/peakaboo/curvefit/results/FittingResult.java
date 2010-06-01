package peakaboo.curvefit.results;


import java.util.List;

import peakaboo.curvefit.fitting.TransitionSeriesFitting;
import peakaboo.datatypes.peaktable.TransitionSeries;

/**
 * 
 * This class stores the results of a {@link TransitionSeriesFitting}, as well as the {@link TransitionSeries}
 * that it applies to.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class FittingResult
{

	public List<Double>		fit;
	public TransitionSeries	transitionSeries;
	public double			scaleFactor;
	public double			normalizationScale;


	public FittingResult(List<Double> fit, TransitionSeries transitionSeries, double scaleFactor, double normalizationScale)
	{
	
		this.fit = fit;
		this.transitionSeries = transitionSeries;
		this.scaleFactor = scaleFactor;
		this.normalizationScale = normalizationScale;

	}

}
