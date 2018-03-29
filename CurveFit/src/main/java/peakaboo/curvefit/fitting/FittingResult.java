package peakaboo.curvefit.fitting;


import peakaboo.curvefit.transitionseries.TransitionSeries;
import scitypes.ReadOnlySpectrum;
import scitypes.Spectrum;

/**
 * 
 * This class stores the results of applying a Spectrum to a {@link CurveFitter}, 
 * as well as the {@link TransitionSeries} that generated the fitter.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class FittingResult
{

	private ReadOnlySpectrum		fit;
	private CurveFitter	fitter;

	private float					fitScale;
	private float					normalizationScale;
	
	private TransitionSeries		transitionSeries;

	public FittingResult(ReadOnlySpectrum fit, CurveFitter fitter, float fitScale)	{
		this.fit = fit;
		this.fitter = fitter;
		this.fitScale = fitScale;
		this.normalizationScale = fitter.getNormalizationScale();
		this.transitionSeries = fitter.getTransitionSeries();
	}


	public ReadOnlySpectrum getFit() {
		return fit;
	}


	public CurveFitter getFitter() {
		return fitter;
	}


	public float getFitScale() {
		return fitScale;
	}
	
	public float normalizationScale() {
		return normalizationScale;
	}
	
	public float getTotalScale() {
		return fitScale / normalizationScale;
	}
	
	public TransitionSeries getTransitionSeries() {
		return transitionSeries;
	}
	
	

}
