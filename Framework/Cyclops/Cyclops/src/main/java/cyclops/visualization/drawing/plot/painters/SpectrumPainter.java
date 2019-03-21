package cyclops.visualization.drawing.plot.painters;



import cyclops.Coord;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import cyclops.visualization.Surface;
import cyclops.visualization.drawing.DrawingRequest;
import cyclops.visualization.drawing.painters.PainterData;
import cyclops.visualization.drawing.plot.painters.PlotPainter.TraceType;


public abstract class SpectrumPainter extends PlotPainter
{

	protected ReadOnlySpectrum data;
	protected TraceType traceType = TraceType.CONNECTED;
	
	public SpectrumPainter(ReadOnlySpectrum data)
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

	
	public TraceType getTraceType() {
		return traceType;
	}

	public void setTraceType(TraceType traceType) {
		this.traceType = traceType;
	}
	
	public SpectrumPainter withTraceType(TraceType traceType) {
		setTraceType(traceType);
		return this;
	}

	
}
