package org.peakaboo.curvefit.curve.fitting;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;

public interface FittingResultView {

	ReadOnlySpectrum getFit();

	CurveView getCurve();

	float getCurveScale();

	float getNormalizationScale();

	ITransitionSeries getTransitionSeries();

	

	default float getFitSum() {
		return getCurve().scaleSum(getCurveScale());
	}

	default float getFitMax() {
		return getCurve().scaleMax(getCurveScale());
	}
	
	default int getFitChannels() {
		return getCurve().get().size();
	}

	default float getTotalScale() {
		return getCurveScale() / getNormalizationScale();
	}
	
	
}