package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot;


import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.SpectrumPainter;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;


public class AreaPainter extends SpectrumPainter
{

	private PaletteColour topColour, bottomColour, strokeColour;

	public AreaPainter(ReadOnlySpectrum data, PaletteColour top, PaletteColour bottom, PaletteColour stroke)
	{
		super(data);
		topColour = top;
		bottomColour = bottom;
		strokeColour = stroke;
	}

	
	public AreaPainter(ReadOnlySpectrum data)
	{
		super(data);
		topColour = new PaletteColour(0xff7f7f7f);
		bottomColour = new PaletteColour(0xff606060);
		strokeColour = new PaletteColour(0xff202020);
	}

	@Override
	public void drawElement(PainterData p)
	{

		traceData(p, traceType);
		p.context.setSourceGradient(0, 0, topColour, 0, p.plotSize.y, bottomColour);
		p.context.fillPreserve();

		// stroke darker
		p.context.setSource(strokeColour);
		p.context.stroke();
		
	}


}
