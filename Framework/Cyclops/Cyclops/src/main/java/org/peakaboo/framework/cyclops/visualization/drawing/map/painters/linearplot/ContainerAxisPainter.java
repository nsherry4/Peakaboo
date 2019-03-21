package org.peakaboo.framework.cyclops.visualization.drawing.map.painters.linearplot;

import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.visualization.drawing.Drawing;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;

public class ContainerAxisPainter extends AxisPainter {

	private Drawing drawing;
	
	private int size;
	private float percent;
	private boolean usePercent;
	
	private Side side;
	
	public enum Side {TOP, BOTTOM, LEFT, RIGHT}
	
	public ContainerAxisPainter(Drawing drawing, float percent, Side side)
	{
		this.drawing = drawing;
		this.percent = percent;
		usePercent = true;
		this.side = side;
		
		
	}
	
	public ContainerAxisPainter(Drawing drawing, int size, Side side) {
		
		this.drawing = drawing;
		this.size = size;
		this.side = side;
		
	}
	
	private float getWidth(PainterData p)
	{
		if (side == Side.TOP || side == Side.BOTTOM)
		{
			return axesData.xPositionBounds.end - axesData.xPositionBounds.start;
		}
		else
		{
			float totalSize = size;
			if (usePercent) totalSize = percent * p.dr.imageWidth;
			return totalSize;			
		}

	}
	private float getHeight(PainterData p)
	{
		if (side == Side.LEFT || side == Side.RIGHT)
		{
			return axesData.yPositionBounds.end - axesData.yPositionBounds.start;
		}
		else
		{
			float totalSize = size;
			if (usePercent) totalSize = percent * p.dr.imageHeight;
			return totalSize;			
		}
	}
	
	@Override
	public Pair<Float, Float> getAxisSizeX(final PainterData p) {
		
		
		
		/*
		Pair<Double, Double> axisSize = Functional.foldr(axisPainters, new Pair<Double, Double>(0d,0d), new Function2<AxisPainter, Pair<Double, Double>, Pair<Double, Double>>() {

			@Override
			public Pair<Double, Double> run(AxisPainter painter, Pair<Double, Double> borders) {
				Pair<Double, Double> xdims = painter.getAxisSizeX(p);
				borders.first += xdims.first;
				borders.second += xdims.second;
				return borders;
			}
		});
		*/
		
		float totalSize = getWidth(p);
		
		if (side == Side.TOP || side == Side.BOTTOM) 
			return new Pair<Float, Float>(0f, 0f);
		else if (side == Side.LEFT) 
			return new Pair<Float, Float>(totalSize, 0f);
		else 
			return new Pair<Float, Float>(0f, totalSize);

	}

	@Override
	public Pair<Float, Float> getAxisSizeY(final PainterData p) {
		
		/*
		Pair<Double, Double> axisSize = Functional.foldr(axisPainters, new Pair<Double, Double>(0d,0d), new Function2<AxisPainter, Pair<Double, Double>, Pair<Double, Double>>() {

			@Override
			public Pair<Double, Double> run(AxisPainter painter, Pair<Double, Double> borders) {
				Pair<Double, Double> ydims = painter.getAxisSizeY(p);
				borders.first += ydims.first;
				borders.second += ydims.second;
				return borders;
			}
		});
		*/
		
		float totalSize = getHeight(p);
		
		if (side == Side.LEFT || side == Side.RIGHT) 
			return new Pair<Float, Float>(0f, 0f);
		else if (side == Side.TOP) 
			return new Pair<Float, Float>(totalSize, 0f);
		else 
			return new Pair<Float, Float>(0f, totalSize);
	}
	
	@Override
	public void drawElement(PainterData p) {
		
		p.context.save();
		
			drawing.setContext(p.context);
			
			if (side == Side.TOP)
			{
				p.context.translate(axesData.xPositionBounds.start, axesData.yPositionBounds.start);
				drawing.getDR().imageHeight = getHeight(p);
				drawing.getDR().imageWidth = getWidth(p);
			}
			else if (side == Side.BOTTOM)
			{
				p.context.translate(axesData.xPositionBounds.start, axesData.yPositionBounds.end - getHeight(p));
				drawing.getDR().imageHeight = getHeight(p);
				drawing.getDR().imageWidth = getWidth(p);
			}
			//TODO: LEFT, RIGHT
					
			drawing.draw();
			
		p.context.restore();
		
	}

}
