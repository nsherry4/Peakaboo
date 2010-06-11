package peakaboo.drawing.plot;


import java.util.List;

import peakaboo.calculations.SpectrumCalculations;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Pair;
import peakaboo.datatypes.Range;
import peakaboo.datatypes.Spectrum;
import peakaboo.drawing.Drawing;
import peakaboo.drawing.DrawingRequest;
import peakaboo.drawing.backends.Surface;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.painters.axis.AxisPainter;
import peakaboo.drawing.plot.painters.PlotPainter;
import peakaboo.drawing.plot.painters.plot.LinePainter;

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
		super(PlotDrawingRequestFactory.getDrawingRequest());
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
		super(PlotDrawingRequestFactory.getDrawingRequest());
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
		super(PlotDrawingRequestFactory.getDrawingRequest());
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
		axisPainters = DataTypeFactory.<AxisPainter> list();
		axisPainters.add(axisPainter);
	}
	
	public void setPainters(List<PlotPainter> painters)
	{
		this.painters = painters;
	}
	
	public void setPainters(PlotPainter painter)
	{
		painters = DataTypeFactory.<PlotPainter> list();
		painters.add(painter);
	}
	
	
	/**
	 * Draws a plot using the given painters
	 */
	public void draw()
	{
		
		if (context == null) return;
		
		dataHeights = new Spectrum(dr.dataWidth, 0.0f);

		context.setLineWidth(getPenWidth(getBaseUnitSize(dr), dr));
		
		context.save();
	
			Coord<Range<Float>> axisBounds = getPlotOffsetFromBottomLeft();
			Range<Float> availableX = axisBounds.x, availableY = axisBounds.y;
			plotSize = new Coord<Float>(availableX.end - availableX.start, availableY.end - availableY.start); 
		
			// transform to get out past the x axis
			// we can't scale to make the region fit in the imageHeight/Width values
			// since the fonts get all squishy
			//context.translate(axesSize.x, 0);
			context.translate(availableX.start, availableY.start);
	
			// clip the region so that we can't draw outside of it
			context.rectangle(0, 0, plotSize.x, plotSize.y);
			//context.rectangle(0, 0, availableX.end - availableX.start, availableY.end - availableY.start);
			context.clip();
	
			// Draw extensions which request being in front of plot
			if (painters != null) {
	
				for (PlotPainter painter : painters) {
					painter.draw(new PainterData(context, dr, plotSize, dataHeights));
				}
	
			}


		context.restore();

		context.save();
			
			availableX = new Range<Float>(0.0f, dr.imageWidth);
			availableY = new Range<Float>(0.0f, dr.imageHeight);
			if (axisPainters != null) {
				
				Pair<Float, Float> axisSizeX, axisSizeY;
				
				for (AxisPainter axisPainter : axisPainters){
				
					axisPainter.setDimensions( 
							
							new Range<Float>(availableX.start, availableX.end),
							new Range<Float>(availableY.start, availableY.end)
							
					);
					axisPainter.draw(new PainterData(context, dr, plotSize, dataHeights));
	
					
					
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
	public Coord<Range<Float>> getPlotOffsetFromBottomLeft()
	{
		
		Range<Float> availableX, availableY;
		availableX = new Range<Float>(0.0f, dr.imageWidth);
		availableY = new Range<Float>(0.0f, dr.imageHeight);
		PainterData p = new PainterData(context, dr, plotSize, dataHeights);
		
		if (axisPainters != null) {
			
			Pair<Float, Float> axisSizeX, axisSizeY;
			
			for (AxisPainter axisPainter : axisPainters){

				
				axisPainter.setDimensions( 
						
						new Range<Float>(availableX.start, availableX.end),
						new Range<Float>(availableY.start, availableY.end)
						
				);
				
				axisSizeX = axisPainter.getAxisSizeX(p);
				axisSizeY = axisPainter.getAxisSizeY(p);

				availableX.start += axisSizeX.first;
				availableX.end -= axisSizeX.second;
				availableY.start += axisSizeY.first;
				availableY.end -= axisSizeY.second;
				
			}
			
		}
				
		
		return new Coord<Range<Float>>(availableX, availableY);

	}


	public static float getBaseUnitSize(peakaboo.drawing.DrawingRequest dr)
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
	
	public static float getDataScale(DrawingRequest dr, Spectrum data)
	{
		
		float datascale;
		if (dr.maxYIntensity == -1){
			datascale = SpectrumCalculations.max(data);
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
