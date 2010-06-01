package peakaboo.drawing.plot.painters;

import java.util.List;

import peakaboo.datatypes.Coord;
import peakaboo.drawing.DrawingRequest;
import peakaboo.drawing.backends.Surface;
import peakaboo.drawing.painters.PainterData;


public abstract class SpectrumPainter extends PlotPainter
{

	protected List<Double> data;
	
	public SpectrumPainter(List<Double> data)
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
	
	protected void traceData(Surface context, DrawingRequest dr, Coord<Double> plotSize, List<Double> dataHeights, TraceType traceType)
	{
		traceData(context, dr, plotSize, dataHeights, traceType, data);
	}

	
}
