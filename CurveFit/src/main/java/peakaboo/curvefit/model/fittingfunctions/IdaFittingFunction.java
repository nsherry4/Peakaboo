package peakaboo.curvefit.model.fittingfunctions;

public class IdaFittingFunction extends SimpleFittingFunction {

	public IdaFittingFunction(float mean, float fwhm, float height) {
		super(mean, fwhm/1.762747174f, height);
	}

	@Override
	public float getHeightAtPoint(float point) {
		float x = point - mean;
		return height * (float)Math.pow(sech(x/width), 2);
	}

	private float sech(float value) {
		float temp = (float) Math.exp(value);
		return 2 / (temp + 1 / temp);
	}
	
}
