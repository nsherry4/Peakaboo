package org.peakaboo.curvefit.curve.fitting;


import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

/**
 * 
 * This class stores the results of applying a Spectrum to a {@link Curve}, 
 * as well as the {@link ITransitionSeries} that generated the curve.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class FittingResult implements FittingResultView
{

	private SpectrumView		fit = null;
	private CurveView	curve;

	private float					curveScale;
	private float					normalizationScale;
	
	private ITransitionSeries		transitionSeries;

	public FittingResult(CurveView curve, float curveScale)	{
		this.curve = curve;
		this.curveScale = curveScale;
		this.normalizationScale = curve.getNormalizationScale();
		this.transitionSeries = curve.getTransitionSeries();
	}


	@Override
	public SpectrumView getFit() {
		if (fit == null) {
			fit = getCurve().scale(getCurveScale());
		}
		return fit;
	}
	
	@Override
	public float getCurveScale() {
		return curveScale;
	}
	
	@Override
	public CurveView getCurve() {
		return curve;
	}
	
	@Override
	public float getNormalizationScale() {
		return normalizationScale;
	}
	
	@Override
	public ITransitionSeries getTransitionSeries() {
		return transitionSeries;
	}
	
}
