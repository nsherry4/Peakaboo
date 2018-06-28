package peakaboo.curvefit.curve.scoring;

import peakaboo.curvefit.curve.fitting.Curve;
import peakaboo.curvefit.curve.fitting.FittingParameters;
import peakaboo.curvefit.curve.fitting.FittingResult;
import peakaboo.curvefit.curve.fitting.fitter.CurveFitter;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import scitypes.ReadOnlySpectrum;

public class CurveFittingScorer implements Scorer {

	private ThreadLocal<Curve> curve;
	private ReadOnlySpectrum data;
	private CurveFitter fitter;
	
	public CurveFittingScorer(ReadOnlySpectrum data, FittingParameters parameters, CurveFitter fitter) {
		this.data = data;
		this.fitter = fitter;
		this.curve = ThreadLocal.withInitial(() -> new Curve(null, parameters));
	}
	
	@Override
	public float score(TransitionSeries ts) {
		
		curve.get().setTransitionSeries(ts);
		FittingResult result = fitter.fit(data, curve.get());
		return result.getFit().sum();
		
	}

}
