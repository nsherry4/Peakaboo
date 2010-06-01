package peakaboo.drawing.plot.painters.axis;


import java.util.List;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Range;
import peakaboo.drawing.painters.axis.AxisPainter;
import peakaboo.drawing.painters.axis.LineAxisPainter;
import peakaboo.drawing.painters.axis.TitleAxisPainter;


public class AxisFactory
{

	public static List<AxisPainter> getAxisPainterSet(String titleX, String titleY, Range<Double> rightValueBounds,
			Range<Double> bottomValueBounds, Range<Double> topValueBounds, Range<Double> leftValueBounds,
			boolean yLeftLog, boolean yRightLog, boolean borderRight, boolean borderBottom, boolean borderTop, boolean borderleft)
	{

		List<AxisPainter> painters = DataTypeFactory.<AxisPainter> list();

		painters.add(new TitleAxisPainter(1.0, titleX, null, null, titleY));
		
		painters.add(new TickMarkAxisPainter(rightValueBounds, bottomValueBounds, topValueBounds, leftValueBounds,
				yLeftLog, yRightLog));
		
		painters.add(new LineAxisPainter(borderleft, borderRight, borderTop, borderBottom));

		return painters;

	}

}
