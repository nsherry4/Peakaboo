package cyclops.visualization.drawing.plot.painters.axis;

import java.util.List;

import cyclops.Bounds;
import cyclops.Pair;
import cyclops.visualization.drawing.painters.PainterData;
import cyclops.visualization.drawing.plot.PlotDrawing;
import cyclops.visualization.drawing.plot.painters.PlotPainter;
import cyclops.visualization.palette.PaletteColour;


public class GridlinePainter extends PlotPainter
{

	Bounds<Float> valueBounds;
	
	public GridlinePainter(Bounds<Float> valueBounds)
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
		p.context.setSource(new PaletteColour(0x20000000));
		
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
