package peakaboo.drawing.plot.painters.axis;

import java.util.List;

import fava.*;

import peakaboo.datatypes.Range;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.plot.PlotDrawing;
import peakaboo.drawing.plot.painters.PlotPainter;


public class GridlinePainter extends PlotPainter
{

	Range<Float> valueBounds;
	
	public GridlinePainter(Range<Float> valueBounds)
	{
		this.valueBounds = valueBounds;
	}
	
	@Override
	public void drawElement(PainterData p)
	{
		
		float valueRangeStart = valueBounds.start;
		float valueRangeEnd = PlotDrawing.getDataScale(valueBounds.end, false);
		
		int maxTicks = AxisMarkGenerator.getMaxTicksY(p.context, p.plotSize.y);
	
		List<Pair<Float, Integer>> tickData = AxisMarkGenerator.getAxisMarkList(maxTicks, p.plotSize.y, 1, valueRangeStart, valueRangeEnd);
		
		p.context.save();
		p.context.setSource(0.0f, 0.0f, 0.0f, 0.1f);
		
		float tickPercent;
		float yPos;
		for (Pair<Float, Integer> tick : tickData)
		{
			tickPercent = tick.first;
			yPos = tickPercent * p.plotSize.y;
			AxisMarkGenerator.drawTick(p.context, 0, yPos, p.plotSize.x, yPos);
			
		}
		
		p.context.restore();
		
	}


}
