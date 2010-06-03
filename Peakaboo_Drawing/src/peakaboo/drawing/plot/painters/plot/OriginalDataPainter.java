package peakaboo.drawing.plot.painters.plot;


import java.awt.Color;
import java.util.List;

import peakaboo.drawing.painters.PainterData;

public class OriginalDataPainter extends LinePainter
{
	
	public OriginalDataPainter(List<Double> data, boolean isMonochrome)
	{
		super(data, getColour(isMonochrome));
	}
	public OriginalDataPainter(List<Double> data)
	{
		super(data, getColour(false));
	}


	private static Color getColour(boolean isMonochrome)
	{
		if (! isMonochrome) {
			return new Color(0.643f, 0.0f, 0.0f, 0.35f);
		} else {
			return new Color(0.0f, 0.0f, 0.0f, 0.5f);
		}
	}
		
	@Override
	public void drawElement(PainterData p)
	{
		traceData(p, TraceType.CONNECTED);
		p.context.setSource(colour);
		p.context.stroke();
	}

}
