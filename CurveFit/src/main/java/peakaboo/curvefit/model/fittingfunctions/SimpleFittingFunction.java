package peakaboo.curvefit.model.fittingfunctions;

public abstract class SimpleFittingFunction implements FittingFunction {

	protected float mean;
	protected float gamma;
	protected float height;
	
	public SimpleFittingFunction(float mean, float gamma, float height){
		
		this.mean = mean;
		this.gamma = gamma;
		this.height = height;
		
	}
	
}
