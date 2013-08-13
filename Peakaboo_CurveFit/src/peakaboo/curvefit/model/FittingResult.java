package peakaboo.curvefit.model;


import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.curvefit.model.transitionseries.TransitionSeriesFitting;
import scitypes.Spectrum;

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

	public Spectrum			fit;
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
