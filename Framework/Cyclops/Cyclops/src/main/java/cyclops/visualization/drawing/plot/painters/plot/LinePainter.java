package cyclops.visualization.drawing.plot.painters.plot;


import cyclops.ReadOnlySpectrum;
import cyclops.visualization.drawing.painters.PainterData;
import cyclops.visualization.drawing.plot.painters.SpectrumPainter;
import cyclops.visualization.palette.PaletteColour;

public class LinePainter extends SpectrumPainter
{

	protected PaletteColour colour;
		
	public LinePainter(ReadOnlySpectrum data, PaletteColour colour)
	{
		super(data);
		this.colour = colour;
	}
	
	public LinePainter(ReadOnlySpectrum data)
	{
		super(data);
		this.colour = new PaletteColour(0xff000000);
	}
	
	@Override
	public void drawElement(PainterData p)
	{
		traceData(p, traceType);
		p.context.setSource(colour);
		p.context.stroke();
	}
	
	
}
