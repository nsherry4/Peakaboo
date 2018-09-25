package cyclops.visualization.drawing.painters.axis;

import cyclops.Bounds;




public class AxesData
{
	
	public Bounds<Float> xPositionBounds, yPositionBounds;
	public boolean yLeftLog, yRightLog;
	
	public AxesData()
	{
		
	}
	
	public AxesData(
			float yLeftPosition,
			float yRightPosition,
			float xTopPosition,
			float xBottomPosition
	)
	{
		

		xPositionBounds = new Bounds<Float>(yLeftPosition, yRightPosition);
		yPositionBounds = new Bounds<Float>(xTopPosition, xBottomPosition);
		
		yLeftLog = false;
		yRightLog = false;
		
	}

	public AxesData(
			
			Bounds<Float> xPositionBounds, 
			Bounds<Float> yPositionBounds
	)
	{
		
		this.xPositionBounds = xPositionBounds;
		this.yPositionBounds = yPositionBounds;
		
		yLeftLog = false;
		yRightLog = false;

	}
	

		
}
