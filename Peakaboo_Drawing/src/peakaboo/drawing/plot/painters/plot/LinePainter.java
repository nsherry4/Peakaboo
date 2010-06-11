package peakaboo.drawing.plot.painters.plot;


import java.awt.Color;
import java.util.List;

import peakaboo.datatypes.Spectrum;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.plot.painters.SpectrumPainter;

public class LinePainter extends SpectrumPainter
{

	protected Color colour;
	
	
	public LinePainter(Spectrum data, Color colour)
	{
		super(data);
		this.colour = colour;
	}
	
	public LinePainter(Spectrum data)
	{
		super(data);
		this.colour = new Color(0, 0, 0);
	}
	
	@Override
	public void drawElement(PainterData p)
	{
		traceData(p, TraceType.CONNECTED);
		p.context.setSource(colour);
		p.context.stroke();
	}

}
