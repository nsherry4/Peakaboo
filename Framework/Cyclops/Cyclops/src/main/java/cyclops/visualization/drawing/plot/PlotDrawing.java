package cyclops.visualization.drawing.plot;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import cyclops.Bounds;
import cyclops.Coord;
import cyclops.ISpectrum;
import cyclops.Pair;
import cyclops.ReadOnlySpectrum;
import cyclops.Spectrum;
import cyclops.log.CyclopsLog;
import cyclops.visualization.Surface;
import cyclops.visualization.drawing.Drawing;
import cyclops.visualization.drawing.DrawingRequest;
import cyclops.visualization.drawing.ViewTransform;
import cyclops.visualization.drawing.painters.PainterData;
import cyclops.visualization.drawing.painters.axis.AxisPainter;
import cyclops.visualization.drawing.plot.painters.PlotPainter;
import cyclops.visualization.drawing.plot.painters.plot.LinePainter;

/**
 * 
 * This class contains logic for drawing plots.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */


public class PlotDrawing extends Drawing
{

	private Coord<Float>		plotSize;
	private Spectrum			dataHeights;
	
	private List<AxisPainter> 	axisPainters;
	private List<PlotPainter> 	painters;


	/**
	 * Create a plot object
	 * @param context the {@link Surface} to draw to
	 * @param dr the {@link DrawingRequest} that defines how this plot should be drawn
	 */
	public PlotDrawing(Surface context, DrawingRequest dr, List<PlotPainter> painters, List<AxisPainter> axisPainters)
	{
		super(dr);
		this.context = context;
		this.axisPainters = axisPainters;
		this.painters = painters;
	}

	/**
	 * Create a plot object with no {@link AxisPainter}s
	 * @param context the {@link Surface} to draw to
	 * @param dr the {@link DrawingRequest} that defines how this plot should be drawn
	 */
	public PlotDrawing(Surface context, DrawingRequest dr)
	{
		super(dr);
		this.context = context;
		this.axisPainters = null;
		this.painters = null;
	}

	/**
	 * Create a plot object with no {@link AxisPainter}s
	 * @param context the {@link Surface} to draw to
	 */
	public PlotDrawing(Surface context)
	{
		super(new DrawingRequest());
		this.context = context;
		this.axisPainters = null;
		this.painters = null;
	}
	
	/**
	 * Create a plot object with no {@link AxisPainter}s
	 * @param context the {@link Surface} to draw to
	 */
	public PlotDrawing(Spectrum numbers)
	{
		super(new DrawingRequest());
		this.context = null;
		this.axisPainters = null;
		dr.dataHeight = 1;
		dr.dataWidth = numbers.size();
		setPainters(new LinePainter(numbers));
	}
	
	/**
	 * Create a plot object with no {@link AxisPainter}s
	 * @param context the {@link Surface} to draw to
	 */
	public PlotDrawing()
	{
		super(new DrawingRequest());
		this.context = null;
		this.axisPainters = null;
		this.painters = null;
	}
	
	
	/**
	 * Gets the {@link Surface} that plots will be drawn to
	 * @return {@link Surface}
	 */
	public Surface getContext()
	{
		return context;
	}


	/**
	 * Gets the size of the plot to be drawn
	 * @return the current plot size
	 */
	public Coord<Float> getPlotSize()
	{
		return plotSize;
	}

	
	public void setAxisPainters(List<AxisPainter> axisPainters)
	{
		this.axisPainters = axisPainters;
	}
	public void setAxisPainters(AxisPainter axisPainter)
	{
		axisPainters = new ArrayList<AxisPainter>();
		axisPainters.add(axisPainter);
	}
	
	public void setPainters(List<PlotPainter> painters)
	{
		this.painters = painters;
	}
	
	public void setPainters(PlotPainter painter)
	{
		painters = new ArrayList<PlotPainter>();
		painters.add(painter);
	}
	
	
	/**
	 * Draws a plot using the given painters
	 */
	@Override
	public void draw()
	{
		
		if (context == null) return;
		
		
		dataHeights = new ISpectrum(dr.dataWidth, 0.0f);

		context.setLineWidth(getPenWidth(getBaseUnitSize(dr), dr));
		
		context.save();
	
			Coord<Bounds<Float>> axisBounds = getPlotOffsetFromBottomLeft();
			Bounds<Float> availableX = axisBounds.x, availableY = axisBounds.y;
			plotSize = new Coord<Float>(availableX.end - availableX.start, availableY.end - availableY.start); 
			if (plotSize.x <= 0 | plotSize.y <= 0) return;
			
			// transform to get out past the x axis
			// we can't scale to make the region fit in the imageHeight/Width values
			// since the fonts get all squishy
			//context.translate(axesSize.x, 0);
			context.translate(availableX.start, availableY.start);
	
			// clip the region so that we can't draw outside of it
			context.rectAt(0, 0, plotSize.x, plotSize.y);
			//context.rectangle(0, 0, availableX.end - availableX.start, availableY.end - availableY.start);
			context.clip();
	
			// Draw extensions which request being in front of plot
			if (painters != null) {
	
				for (PlotPainter painter : painters) {
					try {
						painter.draw(new PainterData(context, dr, plotSize, dataHeights));
					} catch (Exception e) {
						CyclopsLog.get().log(Level.SEVERE, "Failed to draw painter " + painter.getSourceName(), e);
					}
				}
	
			}


		context.restore();

		context.save();
			
			availableX = new Bounds<Float>(0.0f, dr.imageWidth);
			availableY = new Bounds<Float>(0.0f, dr.imageHeight);
			if (axisPainters != null) {
				
				Pair<Float, Float> axisSizeX, axisSizeY;
				
				for (AxisPainter axisPainter : axisPainters){
				
					axisPainter.setDimensions( 
							
							new Bounds<Float>(availableX.start, availableX.end),
							new Bounds<Float>(availableY.start, availableY.end)
							
					);
					
					try{
						axisPainter.draw(new PainterData(context, dr, plotSize, dataHeights));
					} catch (Exception e) {
						CyclopsLog.get().log(Level.WARNING, "Axis Painter " + axisPainter.getSourceName() + " Failed", e);
					}
					
					
					axisSizeX = axisPainter.getAxisSizeX(new PainterData(context, dr, plotSize, dataHeights));
					axisSizeY = axisPainter.getAxisSizeY(new PainterData(context, dr, plotSize, dataHeights));
	
					availableX.start += axisSizeX.first;
					availableX.end -= axisSizeX.second;
					availableY.start += axisSizeY.first;
					availableY.end -= axisSizeY.second;
					
				}
				
			}
			

		context.restore();

		return;
	}

/*
	private Coord<Double> drawAxes(Coord<Double> axes)
	{

		context.setSource(0.0, 0.0, 0.0);

		context.moveTo(axes.x, 0.0);
		context.lineTo(axes.x, dr.imageHeight - axes.y);
		context.lineTo(dr.imageWidth, dr.imageHeight - axes.y);
		context.stroke();


		drawXAxis(axes);
		drawYAxis(axes);

		return axes;

	}


	private void drawXAxis(Coord<Double> axes)
	{

		context.save();

		// dimensions for various parts of the axis
		double baseSize = getBaseUnitSize(dr);
		double tickSize = getTickSize(baseSize, dr);
		double textHeight = getTickFontHeight(context, dr);

		double plotHeight = dr.imageHeight - axes.y;
		double plotWidth = dr.imageWidth - axes.x;
		double textBaseline = plotHeight + (dr.showAxesTickMarks ? tickSize + textHeight : 0.0);

		// calculate the maximum width of a text entry here
		context.save();
		context.useMonoFont();

		double maxEnergy = (dr.xSizePerChannel * dr.dataWidth);

		int maxValue = (int) (maxEnergy);
		String text = String.valueOf(maxValue);

		double maxWidth = context.getTextWidth(text);

		// this section calculates the energy step for ticks - everything is in
		// ENERGY values, not channel values
		// calculate how many ticks (with text) could fit on the axis
		double maxTicks = plotWidth / (maxWidth * 2.0);
		// we know how many we can fit on to the axis, what is the increment
		// size so we can be near, but not above that number of ticks
		int increment = (int) Math.ceil(maxEnergy / maxTicks);


		String tickText;
		double tickWidth;
		double x;

		if (dr.showAxesTickMarks) {
			for (int curTick = increment; curTick <= maxValue; curTick += increment) {
				x = axes.x + (curTick * (plotWidth / dr.dataWidth) / dr.xSizePerChannel);
				context.moveTo(x, plotHeight);
				context.lineTo(x, plotHeight + tickSize);
				context.stroke();

				tickText = String.valueOf(curTick);
				tickWidth = context.getTextWidth(tickText);
				context.writeText(tickText, x - (tickWidth / 2.0), textBaseline);

			}
		}


		context.setFontSize(FONTSIZE_TITLE * dr.titleScale);
		double titleWidth = context.getTextWidth(dr.xAxisTitle);
		double titleHeight = context.getFontAscent();
		context.writeText(dr.xAxisTitle, axes.x + (plotWidth - titleWidth) / 2.0, textBaseline + titleHeight);


		context.restore();

	}


	private void drawYAxis(Coord<Double> axes)
	{

		context.save();

		context.useSansFont();
		context.setFontSize(FONTSIZE_TITLE * dr.titleScale);

		context.rotate(-3.141592653589793238 / 2.0);

		double titleWidth = context.getTextWidth(dr.yAxisTitle);
		double plotHeight = dr.imageHeight - axes.y;
		double titleStart = plotHeight - ((plotHeight - titleWidth) / 2.0);
		double titleHeight = context.getFontHeight();

		context.writeText(dr.yAxisTitle, -titleStart, titleHeight);

		context.stroke();

		context.restore();

	}
*/

	/**
	 * Calculates the space required for drawing the axes
	 * @return A coordinate pair defining the x and y space consumed by the axes
	 */
	public Coord<Bounds<Float>> getPlotOffsetFromBottomLeft()
	{
		
		Bounds<Float> availableX, availableY;
		availableX = new Bounds<Float>(0.0f, dr.imageWidth);
		availableY = new Bounds<Float>(0.0f, dr.imageHeight);
		PainterData p = new PainterData(context, dr, plotSize, dataHeights);
		
		if (axisPainters != null) {
			
			Pair<Float, Float> axisSizeX, axisSizeY;
			
			for (AxisPainter axisPainter : axisPainters){

				
				axisPainter.setDimensions( 
						
						new Bounds<Float>(availableX.start, availableX.end),
						new Bounds<Float>(availableY.start, availableY.end)
						
				);
				
				axisSizeX = axisPainter.getAxisSizeX(p);
				axisSizeY = axisPainter.getAxisSizeY(p);

				availableX.start += axisSizeX.first;
				availableX.end -= axisSizeX.second;
				availableY.start += axisSizeY.first;
				availableY.end -= axisSizeY.second;
				
			}
			
		}
				
		
		return new Coord<Bounds<Float>>(availableX, availableY);

	}


	public static float getBaseUnitSize(DrawingRequest dr)
	{
		return dr.imageHeight / 350.0f;
	}


	public static float getDataScale(DrawingRequest dr)
	{
		
		float datascale;
		if (dr.maxYIntensity == -1){
			return 0;
		} else {
			datascale = dr.maxYIntensity;
		}
		if (dr.viewTransform == ViewTransform.LOG) {
			datascale = (float)Math.log(datascale);
		}
		datascale *= 1.15;

		return datascale;
	}	
	
	public static float getDataScale(float maxValue, boolean log)
	{
		
		float datascale = maxValue;
		if (log) datascale = (float)Math.log(datascale);
		datascale *= 1.15;
		return datascale;
		
	}	
	
	public static float getDataScale(DrawingRequest dr, ReadOnlySpectrum data)
	{
		
		float datascale;
		if (dr.maxYIntensity == -1){
			datascale = data.max();
		} else {
			datascale = dr.maxYIntensity;
		}
		if (dr.viewTransform == ViewTransform.LOG) {
			datascale = (float)Math.log(datascale);
		}
		datascale *= 1.15;

		return datascale;
	}


}
