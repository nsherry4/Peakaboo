package peakaboo.drawing.plot.painters;


import peakaboo.calculations.SpectrumCalculations;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.Range;
import peakaboo.datatypes.Spectrum;
import peakaboo.drawing.DrawingRequest;
import peakaboo.drawing.backends.Surface;
import peakaboo.drawing.painters.Painter;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.plot.PlotDrawing;
import peakaboo.drawing.plot.ViewTransform;

/**
 * 
 * Painters define a method of drawing a given set of data to a plot.
 * 
 * @author Nathaniel Sherry, 2009
 * @see PainterData
 * @see PlotDrawing
 *
 */

public abstract class PlotPainter extends Painter{
	
	public static enum TraceType {CONNECTED, BAR, LINE}
	
	/**
	 * Causes this Painter to draw its data to the given Surface
	 * @param p the {@link PainterData} structure containing objects and information needed to draw to the plot
	 */
	@Override
	public abstract void drawElement(PainterData p);

	/**
	 * Traces a given list of doubles as points on a plot.
	 * @param p the {@link PainterData} structure containing objects and information needed to draw to the plot
	 * @param data the data series to trace
	 */
	protected void traceData(PainterData p, Spectrum data)
	{
		traceData(p, data, TraceType.CONNECTED);
	}
	
	/**
	 * Traces a given list of doubles as points on a plot.
	 * @param p the {@link PainterData} structure containing objects and information needed to draw to the plot
	 * @param data the data series to trace
	 * @param connected should the data points be drawn as part of a connected series, or should each point be shown as an individual line
	 */
	protected void traceData(PainterData p, Spectrum data, TraceType traceType)
	{
		traceData(p.context, p.dr, p.plotSize, p.dataHeights, traceType, data);
	}
	
	/**
	 * Traces a given list of doubles as points on a plot.
	 * @param context the Surface to draw to
	 * @param dr the DrawingRequest defining how this plot should look
	 * @param plotSize the dimensions of the actual plot, after decorations such as axes
	 * @param dataHeights the maximum height drawn to previously for a given column of data
	 * @param connected should the data points be drawn as part of a connected series, or should each point be shown as an individual line
	 * @param data the data series to trace
	 */
	protected void traceData(Surface context, peakaboo.drawing.DrawingRequest _dr, Coord<Float> plotSize, Spectrum dataHeights, TraceType traceType, Spectrum data)
	{
		
		DrawingRequest dr = (DrawingRequest)_dr;

		if (context == null) return;

		// copy the data
		Spectrum transformedData = transformDataForPlot(dr, data);

		// get the image bounds
		float plotWidth = plotSize.x;
		float plotHeight = plotSize.y;
		float pointWidth = plotWidth / dr.dataWidth;

		// draw the path
		context.moveTo(0.0f, plotHeight);
		float height = plotHeight;
		float pointStart, pointEnd, pointMiddle;
		for (int i = 0; i < transformedData.size(); i++) {
			
			pointStart = pointWidth*i;
			pointEnd = pointStart + pointWidth;
			pointMiddle = (pointStart + pointEnd) / 2.0f;
			height = (transformedData.get(i)) * plotHeight;
			if (dataHeights != null && dataHeights.get(i) < height) dataHeights.set(i, height);
			height = plotHeight - height;
			
			
			if (traceType == TraceType.BAR) {
				
				
				context.moveTo(pointStart, plotHeight);
				if (plotHeight != height){
					context.lineTo(pointStart, height);
					context.lineTo(pointEnd, height);
					context.lineTo(pointEnd, plotHeight);
					context.lineTo(pointStart, plotHeight);
				}
				
			} else if (traceType == TraceType.LINE){
				
				
				context.moveTo(pointMiddle, plotHeight);
				if (plotHeight != height){
					context.lineTo(pointMiddle, height);
				}
				
			} else if (traceType == TraceType.CONNECTED){
				
				context.lineTo(pointMiddle, height);
				
			}

			
		}

		// line to end of plot
		if (traceType == TraceType.CONNECTED){
			context.lineTo(pointWidth * transformedData.size(), height);
			context.lineTo(pointWidth * transformedData.size(), plotHeight+100);	
			context.lineTo(0, plotHeight+100);
		}
	}
	
	

	
	protected Coord<Range<Float>> getTextLabelDimensions(PainterData p, String title, float energy)
	{
		DrawingRequest dr = (DrawingRequest)p.dr;

		float textWidth = p.context.getTextWidth(title);

		float channelSize = p.plotSize.x / dr.dataWidth;
		float centreChannel = (energy / dr.unitSize);

		float titleStart = centreChannel * channelSize;
		titleStart -= (textWidth / 2.0);


		float titleHeight = p.context.getFontHeight();
		float penWidth = getPenWidth(getBaseUnitSize(dr), dr);
		float totalHeight = (titleHeight + penWidth * 2);

		float farLeft = titleStart - penWidth * 2;
		float width = textWidth + penWidth * 4;
		float farRight = farLeft + width;

		float leftChannel = (float)Math.floor(farLeft / channelSize);
		float rightChannel = (float)Math.ceil(farRight / channelSize);
		
		return new Coord<Range<Float>>(new Range<Float>(leftChannel, rightChannel), new Range<Float>(penWidth, totalHeight));
		
	}
	
	/**
	 * Draws a text label on the plot
	 * @param p the {@link PainterData} structure containing objects and information needed to draw to the plot.
	 * @param title the title of the label
	 * @param energy the energy value at which to centre the label
	 */
	protected void drawTextLabel(PainterData p, String title, float energy, float xStart, float yStart)
	{
		
		if (xStart > p.plotSize.x) return;

		if (title != null) {

			p.context.setSource(0.0f, 0.0f, 0.0f);
			p.context.writeText(title, xStart, p.plotSize.y - yStart);
		}

	}
	
	
	protected Spectrum transformDataForPlot(DrawingRequest dr, Spectrum data)
	{

		Spectrum transformedData = new Spectrum(data);
		if (dr.viewTransform == ViewTransform.LOG) transformedData = SpectrumCalculations.logList(transformedData);
		float dataMax = PlotDrawing.getDataScale(dr, data);
		transformedData = SpectrumCalculations.divideBy(transformedData, dataMax);

		return transformedData;
	}
	
	protected float transformValueForPlot(DrawingRequest dr, float value)
	{
		if (dr.viewTransform == ViewTransform.LOG) value = (float)Math.log1p(value);
		float dataMax = PlotDrawing.getDataScale(dr);
		value = value / dataMax;

		return value;
	}
	
	
	public float getChannelAtEnergy(peakaboo.drawing.DrawingRequest _dr, float energy)
	{
		DrawingRequest dr = (DrawingRequest)_dr;
		return energy / dr.unitSize;
	}

	public float getXForChannel(PainterData p, float channel)
	{
		float widthPerChannel = p.plotSize.x / p.dr.dataWidth;
		return channel * widthPerChannel;
	}
	
	@Override
	protected float getBaseUnitSize(peakaboo.drawing.DrawingRequest dr)
	{
		return dr.imageHeight / 350.0f;
	}
	
	protected float getPenWidth(float baseSize, peakaboo.drawing.DrawingRequest dr)
	{
		float width;
		width = baseSize;
		return width;
	}
	
}
