package cyclops.visualization.drawing.plot.painters.plot;


import cyclops.ReadOnlySpectrum;
import cyclops.visualization.drawing.painters.PainterData;
import cyclops.visualization.drawing.plot.painters.SpectrumPainter;
import cyclops.visualization.palette.PaletteColour;


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

		if (p.dr.drawToVectorSurface) {
			// fill with flat green - this is due to a bug with pdf rendering in
			// Poppler when showing gradients
			p.context.setSource(bottomColour);
			p.context.fillPreserve();

		} else {

			p.context.setSourceGradient(0, 0, topColour, 0, p.plotSize.y, bottomColour);
			p.context.fillPreserve();

		}

		// stroke darker
		p.context.setSource(strokeColour);
		p.context.stroke();
		
	}


}
