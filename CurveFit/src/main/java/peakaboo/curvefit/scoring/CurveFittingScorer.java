package peakaboo.curvefit.scoring;

import peakaboo.curvefit.fitting.Curve;
import peakaboo.curvefit.fitting.FittingParameters;
import peakaboo.curvefit.fitting.FittingResult;
import peakaboo.curvefit.transition.TransitionSeries;
import scitypes.ReadOnlySpectrum;

public class CurveFittingScorer implements Scorer {

	private Curve curve;
	private ReadOnlySpectrum data;
	
	public CurveFittingScorer(ReadOnlySpectrum data, FittingParameters parameters) {
		this.data = data;
		this.curve = new Curve(null, parameters);
	}
	
	@Override
	public synchronized float score(TransitionSeries ts) {
		
		curve.setTransitionSeries(ts);
		FittingResult result = curve.fit(data);
		return result.getFit().sum();
		
	}

}
