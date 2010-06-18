package peakaboo.drawing.plot.painters;


import peakaboo.datatypes.Coord;
import peakaboo.datatypes.Spectrum;
import peakaboo.drawing.DrawingRequest;
import peakaboo.drawing.backends.Surface;
import peakaboo.drawing.painters.PainterData;


public abstract class SpectrumPainter extends PlotPainter
{

	protected Spectrum data;
	
	public SpectrumPainter(Spectrum data)
	{
		this.data = data;
	}

	protected void traceData(PainterData p)
	{
		traceData(p, data);
	}
	
	protected void traceData(PainterData p, TraceType traceType)
	{
		traceData(p, data, traceType);
	}
	
	protected void traceData(Surface context, DrawingRequest dr, Coord<Float> plotSize, Spectrum dataHeights, TraceType traceType)
	{
		traceData(context, dr, plotSize, dataHeights, traceType, data);
	}

	
}
