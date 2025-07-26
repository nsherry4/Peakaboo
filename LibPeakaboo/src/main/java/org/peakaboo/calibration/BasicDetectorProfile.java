package org.peakaboo.calibration;


import java.io.IOException;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.druthers.DruthersStorable;


public class BasicDetectorProfile implements DetectorProfile {

	protected String name = "None";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean contains(ITransitionSeries ts) {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public float calibrate(float value, ITransitionSeries ts) {
		return value;
	}
	
	@Override
	public SpectrumView calibrateMap(SpectrumView data, ITransitionSeries ts) {
		return data;
	}

	@Override
	public String storeV1() {
		return "";
	}

	@Override
	public DetectorProfile loadV1(String yaml) {
		return this;
	}

	@Override
	public DetectorProfile load(String yaml) throws IOException {
		return this;
	}

	@Override
	public String save() {
		return "";
	}

}
