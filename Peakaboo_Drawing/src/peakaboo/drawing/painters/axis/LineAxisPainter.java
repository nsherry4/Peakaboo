package peakaboo.drawing.painters.axis;

import fava.*;
import fava.datatypes.Pair;

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
			p.context.setSource(0.0f, 0.0f, 0.0f);
	
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
	public Pair<Float, Float> getAxisSizeX(PainterData p)
	{
		
		float penWidth = (float)Math.floor(getPenWidth(getBaseUnitSize(p.dr), p.dr)/2.0);
		return new Pair<Float, Float>(left ? penWidth : 0.0f, right ? penWidth : 0.0f);
	}

	@Override
	public Pair<Float, Float> getAxisSizeY(PainterData p)
	{
		float penWidth = (float)Math.floor(getPenWidth(getBaseUnitSize(p.dr), p.dr)/2.0);		
		return new Pair<Float, Float>(top ? penWidth : 0.0f, bottom ? penWidth : 0.0f);
	}

}
