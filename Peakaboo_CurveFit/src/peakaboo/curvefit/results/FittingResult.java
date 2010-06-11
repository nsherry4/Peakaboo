package peakaboo.curvefit.results;


import java.util.List;

import peakaboo.curvefit.fitting.TransitionSeriesFitting;
import peakaboo.datatypes.Spectrum;
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

	public Spectrum		fit;
	public TransitionSeries	transitionSeries;
	public float			scaleFactor;
	public float			normalizationScale;


	public FittingResult(Spectrum fit, TransitionSeries transitionSeries, float scaleFactor, float normalizationScale)
	{
	
		this.fit = fit;
		this.transitionSeries = transitionSeries;
		this.scaleFactor = scaleFactor;
		this.normalizationScale = normalizationScale;

	}

}
