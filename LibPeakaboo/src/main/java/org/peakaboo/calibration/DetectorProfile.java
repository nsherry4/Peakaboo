package org.peakaboo.calibration;

import java.io.IOException;

import org.peakaboo.curvefit.curve.fitting.FittingResultView;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;

public interface DetectorProfile {

	String getName();

	void setName(String name);

	boolean contains(ITransitionSeries ts);

	boolean isEmpty();

	String storeV1();
	@Deprecated
	DetectorProfile loadV1(String yaml) throws IOException;
	
	DetectorProfile load(String yaml) throws IOException;
	
	public float calibrate(float value, ITransitionSeries ts);
	default float calibratedSum(FittingResultView fittingResult) {
		ITransitionSeries ts = fittingResult.getTransitionSeries();
		float rawfit = fittingResult.getFitSum();
		return calibrate(rawfit, ts);
	}
	public SpectrumView calibrateMap(SpectrumView data, ITransitionSeries ts);
	
	

}