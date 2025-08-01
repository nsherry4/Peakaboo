package org.peakaboo.calibration;

import java.io.IOException;

import org.peakaboo.curvefit.curve.fitting.FittingResultView;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.druthers.DruthersStorable;

public interface DetectorProfile {

	String getName();

	void setName(String name);

	boolean contains(ITransitionSeries ts);

	boolean isEmpty();

	@Deprecated(since="6", forRemoval = true)
	String storeV1();
	@Deprecated(since="6", forRemoval = true)
	DetectorProfile loadV1(String yaml) throws IOException;
	
	DetectorProfile load(String yaml) throws IOException;

	String save();

	float calibrate(float value, ITransitionSeries ts);

	default float calibratedSum(FittingResultView fittingResult) {
		ITransitionSeries ts = fittingResult.getTransitionSeries();
		float rawfit = fittingResult.getFitSum();
		return calibrate(rawfit, ts);
	}

	SpectrumView calibrateMap(SpectrumView data, ITransitionSeries ts);
	
	

}