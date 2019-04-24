package org.peakaboo.framework.cyclops.visualization.drawing.painters.axis;

import org.peakaboo.framework.cyclops.Bounds;




public class AxesData
{
	
	public Bounds<Float> xPositionBounds, yPositionBounds;
	
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

	}

	public AxesData(
			
			Bounds<Float> xPositionBounds, 
			Bounds<Float> yPositionBounds
	)
	{
		
		this.xPositionBounds = xPositionBounds;
		this.yPositionBounds = yPositionBounds;

	}
	

		
}
