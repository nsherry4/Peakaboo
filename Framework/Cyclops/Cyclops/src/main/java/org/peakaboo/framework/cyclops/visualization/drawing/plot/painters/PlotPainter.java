package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters;


import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.cyclops.spectrum.ArraySpectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumView;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;
import org.peakaboo.framework.cyclops.visualization.drawing.ViewTransform;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.Painter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.PlotDrawing;

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
	protected void traceData(PainterData p, SpectrumView data)
	{
		traceData(p, data, TraceType.CONNECTED);
	}
	
	/**
	 * Traces a given list of doubles as points on a plot.
	 * @param p the {@link PainterData} structure containing objects and information needed to draw to the plot
	 * @param data the data series to trace
	 * @param connected should the data points be drawn as part of a connected series, or should each point be shown as an individual line
	 */
	protected void traceData(PainterData p, SpectrumView data, TraceType traceType)
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
	protected void traceData(Surface context, DrawingRequest dr, Coord<Float> plotSize, Spectrum dataHeights, TraceType traceType, SpectrumView data)
	{


		if (context == null || data == null) return;

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
			if (dataHeights != null && dataHeights.get(i) < height) 
			{
				dataHeights.set(i, height);
			}
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
				
				context.lineTo(pointStart, height);
				
			}

			
		}

		// line to end of plot
		if (traceType == TraceType.CONNECTED){
			context.lineTo(pointWidth * transformedData.size(), height);
			context.lineTo(pointWidth * transformedData.size(), plotHeight+100);	
			context.lineTo(0, plotHeight+100);
		}
	}
	
	
	
	protected Spectrum transformDataForPlot(DrawingRequest dr, SpectrumView data)
	{

		Spectrum transformedData = new ArraySpectrum(data);
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

	@Override
	protected float getBaseUnitSize(DrawingRequest dr)
	{
		return dr.imageHeight / 350.0f;
	}
	
	
}
