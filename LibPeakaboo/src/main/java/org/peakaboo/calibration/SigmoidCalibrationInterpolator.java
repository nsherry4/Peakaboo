package org.peakaboo.calibration;

public class SigmoidCalibrationInterpolator extends LinearCalibrationInterpolator {

	@Override
	protected float curve(float x) {
		return (float)( 1-((1+Math.cos(x*3.14))/2) );
	}
		
}
