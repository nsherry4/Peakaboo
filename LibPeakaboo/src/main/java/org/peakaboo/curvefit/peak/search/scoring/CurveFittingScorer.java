package org.peakaboo.curvefit.peak.search.scoring;

import org.peakaboo.curvefit.curve.fitting.Curve;
import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.curve.fitting.ROFittingParameters;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;

public class CurveFittingScorer implements FittingScorer {

	private ThreadLocal<Curve> curve;
	private ReadOnlySpectrum data;
	private CurveFitter fitter;
	private float max;
	
	public CurveFittingScorer(ReadOnlySpectrum data, ROFittingParameters parameters, CurveFitter fitter) {
		this.data = data;
		this.fitter = fitter;
		this.curve = ThreadLocal.withInitial(() -> new Curve(null, parameters));
		this.max = data.max();
	}
	
	@Override
	public float score(ITransitionSeries ts) {
		
		curve.get().setTransitionSeries(ts);
		FittingResult result = fitter.fit(data, curve.get());
		float score = result.getCurveScale() / max;
		score = (float) Math.sqrt(score);
		return score;
		
	}

}
