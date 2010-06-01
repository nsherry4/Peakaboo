package peakaboo.drawing.map.painters.linearplot;

import peakaboo.datatypes.Pair;
import peakaboo.drawing.Drawing;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.painters.axis.AxisPainter;

public class ContainerAxisPainter extends AxisPainter {

	private Drawing drawing;
	
	private int size;
	private double percent;
	private boolean usePercent;
	
	private Side side;
	
	public enum Side {TOP, BOTTOM, LEFT, RIGHT};
	
	public ContainerAxisPainter(Drawing drawing, double percent, Side side)
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
	
	private double getWidth(PainterData p)
	{
		if (side == Side.TOP || side == Side.BOTTOM)
		{
			return axesData.xPositionBounds.end - axesData.xPositionBounds.start;
		}
		else
		{
			double totalSize = size;
			if (usePercent) totalSize = percent * p.dr.imageWidth;
			return totalSize;			
		}

	}
	private double getHeight(PainterData p)
	{
		if (side == Side.LEFT || side == Side.RIGHT)
		{
			return axesData.yPositionBounds.end - axesData.yPositionBounds.start;
		}
		else
		{
			double totalSize = size;
			if (usePercent) totalSize = percent * p.dr.imageHeight;
			return totalSize;			
		}
	}
	
	@Override
	public Pair<Double, Double> getAxisSizeX(final PainterData p) {
		
		
		
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
		
		double totalSize = getWidth(p);
		
		if (side == Side.TOP || side == Side.BOTTOM) 
			return new Pair<Double, Double>(0d, 0d);
		else if (side == Side.LEFT) 
			return new Pair<Double, Double>(totalSize, 0d);
		else 
			return new Pair<Double, Double>(0d, totalSize);

	}

	@Override
	public Pair<Double, Double> getAxisSizeY(final PainterData p) {
		
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
		
		double totalSize = getHeight(p);
		
		if (side == Side.LEFT || side == Side.RIGHT) 
			return new Pair<Double, Double>(0d, 0d);
		else if (side == Side.TOP) 
			return new Pair<Double, Double>(totalSize, 0d);
		else 
			return new Pair<Double, Double>(0d, totalSize);
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
