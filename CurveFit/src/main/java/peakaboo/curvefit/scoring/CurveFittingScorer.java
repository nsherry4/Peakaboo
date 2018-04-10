package peakaboo.curvefit.scoring;

import peakaboo.curvefit.fitting.Curve;
import peakaboo.curvefit.fitting.FittingParameters;
import peakaboo.curvefit.fitting.FittingResult;
import peakaboo.curvefit.transition.TransitionSeries;
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
		FittingResult result = curve.get().fit(data);
		return result.getFit().sum();
		
	}

}
