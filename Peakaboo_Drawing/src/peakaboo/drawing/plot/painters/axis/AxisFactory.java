package peakaboo.drawing.plot.painters.axis;


import java.util.List;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Range;
import peakaboo.drawing.painters.axis.AxisPainter;
import peakaboo.drawing.painters.axis.LineAxisPainter;
import peakaboo.drawing.painters.axis.TitleAxisPainter;


public class AxisFactory
{

	public static List<AxisPainter> getAxisPainterSet(String titleX, String titleY, Range<Float> rightValueBounds,
			Range<Float> bottomValueBounds, Range<Float> topValueBounds, Range<Float> leftValueBounds,
			boolean yLeftLog, boolean yRightLog, boolean borderRight, boolean borderBottom, boolean borderTop, boolean borderleft)
	{

		List<AxisPainter> painters = DataTypeFactory.<AxisPainter> list();

		painters.add(new TitleAxisPainter(1.0f, titleX, null, null, titleY));
		
		painters.add(new TickMarkAxisPainter(rightValueBounds, bottomValueBounds, topValueBounds, leftValueBounds,
				yLeftLog, yRightLog));
		
		painters.add(new LineAxisPainter(borderleft, borderRight, borderTop, borderBottom));

		return painters;

	}

}
