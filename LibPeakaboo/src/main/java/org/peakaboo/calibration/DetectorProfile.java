package org.peakaboo.calibration;

import java.io.IOException;

import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;

public interface DetectorProfile {

	String getName();

	void setName(String name);

	boolean contains(ITransitionSeries ts);

	boolean isEmpty();

	String save();
	DetectorProfile load(String yaml) throws IOException;
	
	public float calibrate(float value, ITransitionSeries ts);
	default float calibratedSum(FittingResult fittingResult) {
		ITransitionSeries ts = fittingResult.getTransitionSeries();
		float rawfit = fittingResult.getFitSum();
		return calibrate(rawfit, ts);
	}
	public ReadOnlySpectrum calibrateMap(ReadOnlySpectrum data, ITransitionSeries ts);
	
	

}