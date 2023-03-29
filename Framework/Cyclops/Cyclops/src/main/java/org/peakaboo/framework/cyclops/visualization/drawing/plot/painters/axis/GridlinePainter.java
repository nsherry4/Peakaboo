package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis;

import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.PlotPainter;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;


/**
 * Draws grid lines based on a TickFormatter. Currently only supports horitontal lines.
 */
public class GridlinePainter extends PlotPainter
{

	TickFormatter tick;
	
	public GridlinePainter(TickFormatter tick)
	{
		this.tick = tick;
	}
	
	@Override
	public void drawElement(PainterData p)
	{
		
		p.context.save();
		p.context.setSource(new PaletteColour(0x20000000));
		
		for (var mark : tick.getTickMarks(p, p.plotSize.y)) {
			var yPos = (1f - mark.position()) * p.plotSize.y;
			TickMarkAxisPainter.drawTickLine(p.context, 0, yPos, p.plotSize.x, yPos);
		}
		
		p.context.restore();
		
	}


}
