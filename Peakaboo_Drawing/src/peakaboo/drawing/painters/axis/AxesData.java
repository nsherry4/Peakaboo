package peakaboo.drawing.painters.axis;

import peakaboo.datatypes.Range;


public class AxesData
{
	
	public Range<Float> xPositionBounds, yPositionBounds;
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
		

		xPositionBounds = new Range<Float>(yLeftPosition, yRightPosition);
		yPositionBounds = new Range<Float>(xTopPosition, xBottomPosition);
		
		yLeftLog = false;
		yRightLog = false;
		
	}

	public AxesData(
			
			Range<Float> xPositionBounds, 
			Range<Float> yPositionBounds
	)
	{
		
		this.xPositionBounds = xPositionBounds;
		this.yPositionBounds = yPositionBounds;
		
		yLeftLog = false;
		yRightLog = false;

	}
	

		
}
