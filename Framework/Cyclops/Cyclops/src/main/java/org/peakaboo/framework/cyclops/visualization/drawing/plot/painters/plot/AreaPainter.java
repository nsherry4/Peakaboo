package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot;


import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.SpectrumPainter;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;


public class AreaPainter extends SpectrumPainter
{

	private PaletteColour fillColour, strokeColour;

	public AreaPainter(SpectrumView data, PaletteColour fill, PaletteColour stroke)
	{
		super(data);
		fillColour = fill;
		strokeColour = stroke;
	}

	
	public AreaPainter(SpectrumView data)
	{
		super(data);
		fillColour = new PaletteColour(0xff7f7f7f);
		strokeColour = new PaletteColour(0xff202020);
	}

	@Override
	public void drawElement(PainterData p)
	{

		traceData(p, traceType);
		p.context.setSource(this.fillColour);
		p.context.fillPreserve();

		// stroke darker
		p.context.setSource(strokeColour);
		p.context.stroke();
		
	}


}
