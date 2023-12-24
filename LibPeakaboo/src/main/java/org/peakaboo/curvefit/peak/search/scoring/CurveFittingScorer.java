package org.peakaboo.curvefit.peak.search.scoring;

import org.peakaboo.curvefit.curve.fitting.Curve;
import org.peakaboo.curvefit.curve.fitting.FittingParametersView;
import org.peakaboo.curvefit.curve.fitting.FittingResultView;
import org.peakaboo.curvefit.curve.fitting.fitter.CurveFitterPlugin;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;

public class CurveFittingScorer implements FittingScorer {

	private ThreadLocal<Curve> curve;
	private ReadOnlySpectrum data;
	private CurveFitterPlugin fitter;
	private float max;
	
	public CurveFittingScorer(ReadOnlySpectrum data, FittingParametersView parameters, CurveFitterPlugin fitter) {
		this.data = data;
		this.fitter = fitter;
		this.curve = ThreadLocal.withInitial(() -> new Curve(null, parameters));
		this.max = data.max();
	}
	
	@Override
	public float score(ITransitionSeries ts) {
		
		curve.get().setTransitionSeries(ts);
		FittingResultView result = fitter.fit(data, curve.get());
		float score = result.getCurveScale() / max;
		score = (float) Math.sqrt(score);
		return score;
		
	}

}
