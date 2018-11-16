package peakaboo.curvefit.curve.fitting;


import cyclops.ReadOnlySpectrum;
import peakaboo.curvefit.peak.transition.LegacyTransitionSeries;

/**
 * 
 * This class stores the results of applying a Spectrum to a {@link Curve}, 
 * as well as the {@link LegacyTransitionSeries} that generated the curve.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class FittingResult
{

	private ReadOnlySpectrum		fit;
	private Curve	curve;

	private float					curveScale;
	private float					normalizationScale;
	
	private LegacyTransitionSeries		transitionSeries;

	public FittingResult(ReadOnlySpectrum fit, Curve curve, float curveScale)	{
		this.fit = fit;
		this.curve = curve;
		this.curveScale = curveScale;
		this.normalizationScale = curve.getNormalizationScale();
		this.transitionSeries = curve.getTransitionSeries();
	}


	public ReadOnlySpectrum getFit() {
		return fit;
	}


	public Curve getCurve() {
		return curve;
	}


	public float getCurveScale() {
		return curveScale;
	}
	
	public float getNormalizationScale() {
		return normalizationScale;
	}
	
	public float getTotalScale() {
		return curveScale / normalizationScale;
	}
	
	public LegacyTransitionSeries getTransitionSeries() {
		return transitionSeries;
	}
	
	

}
