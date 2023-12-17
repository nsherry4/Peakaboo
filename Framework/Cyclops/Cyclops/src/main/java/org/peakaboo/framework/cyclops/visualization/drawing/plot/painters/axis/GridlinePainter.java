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
		var majorColour = new PaletteColour(0x28000000);
		var minorColour = new PaletteColour(0x10000000);
		
		
		for (var mark : tick.getTickMarks(p, p.plotSize.y, true)) {
			var yPos = (1f - mark.position()) * p.plotSize.y;
			if (mark.minor()) {
				p.context.setSource(minorColour);
			} else {
				p.context.setSource(majorColour);
			}
			TickMarkAxisPainter.drawTickLine(p.context, 0, yPos, p.plotSize.x, yPos);
		}
		
		p.context.restore();
		
	}


}
