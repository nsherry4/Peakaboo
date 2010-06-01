package peakaboo.drawing.painters.axis;

import peakaboo.datatypes.Pair;
import peakaboo.drawing.Drawing;
import peakaboo.drawing.painters.PainterData;


/**
 * Simple {@link AxisPainter} used to draw lines around a {@link Drawing}.
 * @author Nathaniel Sherry, 2009
 *
 */

public class LineAxisPainter extends AxisPainter
{

	private boolean left, right, top, bottom;
	
	public LineAxisPainter(boolean left, boolean right, boolean top, boolean bottom)
	{
		super();
		
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
	}

	@Override
	public void drawElement(PainterData p)
	{

		p.context.save();
			p.context.setSource(0.0, 0.0, 0.0);
	
			p.context.setAntialias(false);
			
			if (top){
				p.context.moveTo(axesData.xPositionBounds.start, axesData.yPositionBounds.start);
				p.context.lineTo(axesData.xPositionBounds.end, axesData.yPositionBounds.start);
			}
			
			if (bottom){
				p.context.moveTo(axesData.xPositionBounds.start, axesData.yPositionBounds.end);
				p.context.lineTo(axesData.xPositionBounds.end, axesData.yPositionBounds.end);
			}
			
			if (right){
				p.context.moveTo(axesData.xPositionBounds.end, axesData.yPositionBounds.start);
				p.context.lineTo(axesData.xPositionBounds.end, axesData.yPositionBounds.end);
			}
			
			if (left){
				p.context.moveTo(axesData.xPositionBounds.start, axesData.yPositionBounds.start);
				p.context.lineTo(axesData.xPositionBounds.start, axesData.yPositionBounds.end);
			}
	
			p.context.stroke();
			
		p.context.restore();
		
	}

	@Override
	public Pair<Double, Double> getAxisSizeX(PainterData p)
	{
		
		double penWidth = Math.floor(getPenWidth(getBaseUnitSize(p.dr), p.dr)/2.0);
		return new Pair<Double, Double>(left ? penWidth : 0.0, right ? penWidth : 0.0);
	}

	@Override
	public Pair<Double, Double> getAxisSizeY(PainterData p)
	{
		double penWidth = Math.floor(getPenWidth(getBaseUnitSize(p.dr), p.dr)/2.0);		
		return new Pair<Double, Double>(top ? penWidth : 0.0, bottom ? penWidth : 0.0);
	}

}
