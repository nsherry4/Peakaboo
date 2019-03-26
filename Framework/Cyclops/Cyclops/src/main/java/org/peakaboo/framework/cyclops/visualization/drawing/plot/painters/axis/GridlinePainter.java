package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis;

import java.util.List;

import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.PlotDrawing;
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
		
		float valueRangeStart = tick.start;
		float valueRangeEnd = PlotDrawing.getDataScale(tick.end, false);
		float maxTicks = tick.calcMaxTicks(p, p.plotSize.y);
	
		List<Pair<Float, Integer>> tickData = AxisMarkGenerator.getAxisMarkList(maxTicks, p.plotSize.y, 1, valueRangeStart, valueRangeEnd);
		
		p.context.save();
		p.context.setSource(new PaletteColour(0x20000000));
		
		float tickPercent;
		float yPos;
		for (Pair<Float, Integer> tick : tickData)
		{
			tickPercent = tick.first;
			yPos = tickPercent * p.plotSize.y;
			AxisMarkGenerator.drawLine(p.context, 0, yPos, p.plotSize.x, yPos);
			
		}
		
		p.context.restore();
		
	}


}
