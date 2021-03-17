package org.peakaboo.curvefit.curve.fitting;


import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;

/**
 * 
 * This class stores the results of applying a Spectrum to a {@link Curve}, 
 * as well as the {@link ITransitionSeries} that generated the curve.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class FittingResult
{

	private ReadOnlySpectrum		fit = null;
	private Curve	curve;

	private float					curveScale;
	private float					normalizationScale;
	
	private ITransitionSeries		transitionSeries;

	public FittingResult(Curve curve, float curveScale)	{
		this.curve = curve;
		this.curveScale = curveScale;
		this.normalizationScale = curve.getNormalizationScale();
		this.transitionSeries = curve.getTransitionSeries();
	}


	public ReadOnlySpectrum getFit() {
		if (fit == null) {
			fit = curve.scale(curveScale);
		}
		return fit;
	}

	public float getFitSum() {
		return curve.scaleSum(curveScale);
	}

	public float getFitMax() {
		return curve.scaleMax(curveScale);
	}
	
	public int getFitChannels() {
		return curve.get().size();
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
	
	public ITransitionSeries getTransitionSeries() {
		return transitionSeries;
	}
	
	

}
