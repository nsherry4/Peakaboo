package peakaboo.drawing.plot.painters.axis;

import java.util.List;

import peakaboo.datatypes.Pair;
import peakaboo.datatypes.Range;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.plot.PlotDrawing;
import peakaboo.drawing.plot.painters.PlotPainter;


public class GridlinePainter extends PlotPainter
{

	Range<Double> valueBounds;
	
	public GridlinePainter(Range<Double> valueBounds)
	{
		this.valueBounds = valueBounds;
	}
	
	@Override
	public void drawElement(PainterData p)
	{
		
		double valueRangeStart = valueBounds.start;
		double valueRangeEnd = PlotDrawing.getDataScale(valueBounds.end, false);
		
		int maxTicks = AxisMarkGenerator.getMaxTicksY(p.context, p.plotSize.y);
	
		List<Pair<Double, Integer>> tickData = AxisMarkGenerator.getAxisMarkList(maxTicks, p.plotSize.y, 1, valueRangeStart, valueRangeEnd);
		
		p.context.save();
		p.context.setSource(0.0, 0.0, 0.0, 0.1);
		
		double tickPercent;
		double yPos;
		for (Pair<Double, Integer> tick : tickData)
		{
			tickPercent = tick.first;
			yPos = tickPercent * p.plotSize.y;
			AxisMarkGenerator.drawTick(p.context, 0, yPos, p.plotSize.x, yPos);
			
		}
		
		p.context.restore();
		
	}


}
