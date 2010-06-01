package peakaboo.drawing.painters.axis;

import peakaboo.datatypes.Range;


public class AxesData
{
	
	public Range<Double> xPositionBounds, yPositionBounds;
	public boolean yLeftLog, yRightLog;
	
	public AxesData()
	{
		
	}
	
	public AxesData(
			double yLeftPosition,
			double yRightPosition,
			double xTopPosition,
			double xBottomPosition
	)
	{
		

		xPositionBounds = new Range<Double>(yLeftPosition, yRightPosition);
		yPositionBounds = new Range<Double>(xTopPosition, xBottomPosition);
		
		yLeftLog = false;
		yRightLog = false;
		
	}

	public AxesData(
			
			Range<Double> xPositionBounds, 
			Range<Double> yPositionBounds
	)
	{
		
		this.xPositionBounds = xPositionBounds;
		this.yPositionBounds = yPositionBounds;
		
		yLeftLog = false;
		yRightLog = false;

	}
	

		
}
