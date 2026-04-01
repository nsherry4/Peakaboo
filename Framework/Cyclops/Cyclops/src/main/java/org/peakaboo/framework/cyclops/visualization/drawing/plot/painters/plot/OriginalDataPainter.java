package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot;


import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

public class OriginalDataPainter extends LinePainter
{

	public OriginalDataPainter(SpectrumView data, PaletteColour colour)
	{
		super(data, colour);
	}
	public OriginalDataPainter(SpectrumView data)
	{
		super(data, new PaletteColour(0x60D32F2F));
	}


	@Override
	public void drawElement(PainterData p)
	{
		traceData(p, TraceType.CONNECTED);
		p.context.setSource(colour);
		p.context.stroke();
	}

}
