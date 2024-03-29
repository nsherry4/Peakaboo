package org.peakaboo.framework.cyclops.visualization.drawing.painters.axis;

import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.visualization.drawing.Drawing;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;


/**
 * Simple {@link AxisPainter} used to draw lines around a {@link Drawing}.
 * @author Nathaniel Sherry, 2009
 *
 */

public class LineAxisPainter extends AxisPainter
{

	private boolean left, right, top, bottom;
	
	private PaletteColour colour;
	
	public LineAxisPainter(PaletteColour colour, boolean left, boolean right, boolean top, boolean bottom)
	{
		super();
		
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		this.colour = colour;
	}

	@Override
	public void drawElement(PainterData p)
	{

		p.context.save();
			p.context.setSource(this.colour);
	
			p.context.setAntialias(false);
			
			// Don't leave too much space around the plot, hug it tightly by flooring/ceiling it
			
			if (top){
				p.context.moveTo(axesData.xPositionBounds.start, (float)Math.ceil(axesData.yPositionBounds.start));
				p.context.lineTo(axesData.xPositionBounds.end, (float)Math.ceil(axesData.yPositionBounds.start));
			}
			
			if (bottom){
				p.context.moveTo(axesData.xPositionBounds.start, (float)Math.floor(axesData.yPositionBounds.end));
				p.context.lineTo(axesData.xPositionBounds.end, (float)Math.floor(axesData.yPositionBounds.end));
			}
			
			if (right){
				p.context.moveTo((float)Math.floor(axesData.xPositionBounds.end), axesData.yPositionBounds.start);
				p.context.lineTo((float)Math.floor(axesData.xPositionBounds.end), axesData.yPositionBounds.end);
			}
			
			if (left){
				p.context.moveTo((float)Math.ceil(axesData.xPositionBounds.start), axesData.yPositionBounds.start);
				p.context.lineTo((float)Math.ceil(axesData.xPositionBounds.start), axesData.yPositionBounds.end);
			}
	
			p.context.stroke();
			
		p.context.restore();
		
	}

	@Override
	public Pair<Float, Float> getAxisSizeX(PainterData p)
	{
		
		float penWidth = (float)Math.floor(getPenWidth(getBaseUnitSize(p.dr))/2.0);
		return new Pair<>(left ? penWidth : 0.0f, right ? penWidth : 0.0f);
	}

	@Override
	public Pair<Float, Float> getAxisSizeY(PainterData p)
	{
		float penWidth = (float)Math.floor(getPenWidth(getBaseUnitSize(p.dr))/2.0);		
		return new Pair<>(top ? penWidth : 0.0f, bottom ? penWidth : 0.0f);
	}

}
