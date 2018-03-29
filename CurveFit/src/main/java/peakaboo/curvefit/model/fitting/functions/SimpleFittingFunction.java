package peakaboo.curvefit.model.fitting.functions;

public abstract class SimpleFittingFunction implements FittingFunction {

	protected float mean;
	protected float width;
	protected float height;
	
	public SimpleFittingFunction(float mean, float width, float height){
		
		this.mean = mean;
		this.width = width;
		this.height = height;
		
	}
	
}
