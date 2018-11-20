package peakaboo.curvefit.peak.search.scoring;

import cyclops.ReadOnlySpectrum;
import peakaboo.curvefit.curve.fitting.Curve;
import peakaboo.curvefit.curve.fitting.FittingParameters;
import peakaboo.curvefit.curve.fitting.FittingResult;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import peakaboo.curvefit.peak.transition.ITransitionSeries;

public class CurveFittingScorer implements FittingScorer {

	private ThreadLocal<Curve> curve;
	private ReadOnlySpectrum data;
	private CurveFitter fitter;
	private float max;
	
	public CurveFittingScorer(ReadOnlySpectrum data, FittingParameters parameters, CurveFitter fitter) {
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
		//float score = result.getFit().sum();
		return score;
		
	}

}
