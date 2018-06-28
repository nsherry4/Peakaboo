package peakaboo.curvefit.curve.scoring;

import peakaboo.curvefit.curve.fitting.Curve;
import peakaboo.curvefit.curve.fitting.FittingParameters;
import peakaboo.curvefit.curve.fitting.FittingResult;
import peakaboo.curvefit.curve.fitting.fitter.UnderCurveFitter;
import peakaboo.curvefit.peak.transition.TransitionSeries;
import scitypes.ReadOnlySpectrum;

public class CurveFittingScorer implements Scorer {

	private ThreadLocal<Curve> curve;
	private ReadOnlySpectrum data;
	
	public CurveFittingScorer(ReadOnlySpectrum data, FittingParameters parameters) {
		this.data = data;
		this.curve = ThreadLocal.withInitial(() -> new Curve(null, parameters));
	}
	
	@Override
	public float score(TransitionSeries ts) {
		
		curve.get().setTransitionSeries(ts);
		FittingResult result = new UnderCurveFitter().fit(data, curve.get());
		return result.getFit().sum();
		
	}

}
