package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot;


import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

public class OriginalDataPainter extends LinePainter
{
	
	public OriginalDataPainter(ReadOnlySpectrum data, boolean isMonochrome)
	{
		super(data, getColour(isMonochrome));
	}
	public OriginalDataPainter(ReadOnlySpectrum data)
	{
		super(data, getColour(false));
	}


	private static PaletteColour getColour(boolean isMonochrome)
	{
		if (! isMonochrome) {
			return new PaletteColour(0x60D32F2F);
		} else {
			return new PaletteColour(0x7f000000);
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
