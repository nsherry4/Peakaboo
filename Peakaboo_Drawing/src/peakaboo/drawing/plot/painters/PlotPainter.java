package peakaboo.drawing.plot.painters;

import java.util.List;

import peakaboo.calculations.ListCalculations;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Range;
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
	protected void traceData(PainterData p, List<Double> data)
	{
		traceData(p, data, TraceType.CONNECTED);
	}
	
	/**
	 * Traces a given list of doubles as points on a plot.
	 * @param p the {@link PainterData} structure containing objects and information needed to draw to the plot
	 * @param data the data series to trace
	 * @param connected should the data points be drawn as part of a connected series, or should each point be shown as an individual line
	 */
	protected void traceData(PainterData p, List<Double> data, TraceType traceType)
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
	protected void traceData(Surface context, peakaboo.drawing.DrawingRequest _dr, Coord<Double> plotSize, List<Double> dataHeights, TraceType traceType, List<Double> data)
	{
		
		DrawingRequest dr = (DrawingRequest)_dr;

		if (context == null) return;

		// copy the data
		List<Double> transformedData = transformDataForPlot(dr, data);

		// get the image bounds
		double plotWidth = plotSize.x;
		double plotHeight = plotSize.y;
		double pointWidth = plotWidth / dr.dataWidth;

		// draw the path
		context.moveTo(0.0, plotHeight);
		double height = plotHeight;
		double pointStart, pointEnd, pointMiddle;
		for (int i = 0; i < transformedData.size(); i++) {
			
			pointStart = pointWidth*i;
			pointEnd = pointStart + pointWidth;
			pointMiddle = (pointStart + pointEnd) / 2.0;
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
	
	

	
	protected Coord<Range<Double>> getTextLabelDimensions(PainterData p, String title, double energy)
	{
		DrawingRequest dr = (DrawingRequest)p.dr;

		double textWidth = p.context.getTextWidth(title);

		double channelSize = p.plotSize.x / dr.dataWidth;
		double centreChannel = (energy / dr.unitSize);

		double titleStart = centreChannel * channelSize;
		titleStart -= (textWidth / 2.0);


		double titleHeight = p.context.getFontHeight();
		double penWidth = getPenWidth(getBaseUnitSize(dr), dr);
		double totalHeight = (titleHeight + penWidth * 2);

		double farLeft = titleStart - penWidth * 2;
		double width = textWidth + penWidth * 4;
		double farRight = farLeft + width;

		double leftChannel = Math.floor(farLeft / channelSize);
		double rightChannel = Math.ceil(farRight / channelSize);
		
		return new Coord<Range<Double>>(new Range<Double>(leftChannel, rightChannel), new Range<Double>(penWidth, totalHeight));
		
	}
	
	/**
	 * Draws a text label on the plot
	 * @param p the {@link PainterData} structure containing objects and information needed to draw to the plot.
	 * @param title the title of the label
	 * @param energy the energy value at which to centre the label
	 */
	protected void drawTextLabel(PainterData p, String title, double energy, double xStart, double yStart)
	{
		
		if (xStart > p.plotSize.x) return;

		if (title != null) {

			p.context.setSource(0.0, 0.0, 0.0);
			p.context.writeText(title, xStart, p.plotSize.y - yStart);
		}

	}
	
	
	protected List<Double> transformDataForPlot(DrawingRequest dr, List<Double> data)
	{

		List<Double> transformedData = DataTypeFactory.<Double> listInit(data);
		if (dr.viewTransform == ViewTransform.LOG) transformedData = ListCalculations.logList(transformedData);
		double dataMax = PlotDrawing.getDataScale(dr, data);
		transformedData = ListCalculations.divideBy(transformedData, dataMax);

		return transformedData;
	}
	
	protected double transformValueForPlot(DrawingRequest dr, double value)
	{
		if (dr.viewTransform == ViewTransform.LOG) value = Math.log1p(value);
		double dataMax = PlotDrawing.getDataScale(dr);
		value = value / dataMax;

		return value;
	}
	
	
	public double getChannelAtEnergy(peakaboo.drawing.DrawingRequest _dr, double energy)
	{
		DrawingRequest dr = (DrawingRequest)_dr;
		return energy / dr.unitSize;
	}

	public double getXForChannel(PainterData p, double channel)
	{
		double widthPerChannel = p.plotSize.x / p.dr.dataWidth;
		return channel * widthPerChannel;
	}
	
	@Override
	protected double getBaseUnitSize(peakaboo.drawing.DrawingRequest dr)
	{
		return dr.imageHeight / 350.0;
	}
	
	protected double getPenWidth(double baseSize, peakaboo.drawing.DrawingRequest dr)
	{
		double width;
		width = baseSize;
		return width;
	}
	
}
