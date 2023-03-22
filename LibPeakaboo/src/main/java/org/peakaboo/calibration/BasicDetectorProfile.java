package org.peakaboo.calibration;


import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;


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
	public ReadOnlySpectrum calibrateMap(ReadOnlySpectrum data, ITransitionSeries ts) {
		return data;
	}

	@Override
	public String save() {
		return "";
	}

	@Override
	public DetectorProfile load(String yaml) {
		return this;
	}
	
}
