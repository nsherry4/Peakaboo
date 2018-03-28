package peakaboo.curvefit.model.fittingfunctions;

public class MixedFittingFunction implements FittingFunction {

	private FittingFunction f1, f2;
	private float percentF1;
	
	public MixedFittingFunction(FittingFunction f1, FittingFunction f2, float percentF1) {
		this.f1 = f1;
		this.f2 = f2;
		this.percentF1 = percentF1;
	}

	@Override
	public float getHeightAtPoint(float point) {
		float f1v = this.f1.getHeightAtPoint(point) * percentF1;
		float f2v = this.f2.getHeightAtPoint(point) * (1f - percentF1);
		return f1v + f2v;
	}
	
}
