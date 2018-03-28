package peakaboo.curvefit.model.fittingfunctions;

public class IdaFittingFunction extends SimpleFittingFunction {

	public IdaFittingFunction(float mean, float gamma, float height) {
		super(mean, gamma*1.05f, height);
	}

	@Override
	public float getHeightAtPoint(float point) {
		float x = point - mean;
		//return height * (float)((gamma/2f)*Math.pow((1+Math.pow((x/gamma), 2)), -1.5f));
		return height * (float)Math.pow(sech(x/gamma), 2);
	}

	private float sech(float value) {
		float temp = (float) Math.exp(value);
		return 2 / (temp + 1 / temp);
	}
	
}
