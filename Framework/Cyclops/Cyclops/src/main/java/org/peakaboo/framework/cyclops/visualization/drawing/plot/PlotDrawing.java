package org.peakaboo.framework.cyclops.visualization.drawing.plot;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.log.CyclopsLog;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.Drawing;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;
import org.peakaboo.framework.cyclops.visualization.drawing.ViewTransform;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.PlotPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.LinePainter;

/**
 * 
 * This class contains logic for drawing plots.
 * 
 * @author Nathaniel Sherry, 2009-2023
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
	public PlotDrawing(Surface context, DrawingRequest dr, List<PlotPainter> painters, List<AxisPainter> axisPainters) {
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
	public PlotDrawing(Surface context, DrawingRequest dr) {
		super(dr);
		this.context = context;
		this.axisPainters = null;
		this.painters = null;
	}

	/**
	 * Create a plot object with no {@link AxisPainter}s
	 * @param context the {@link Surface} to draw to
	 */
	public PlotDrawing(Surface context) {
		super(new DrawingRequest());
		this.context = context;
		this.axisPainters = null;
		this.painters = null;
	}
	
	/**
	 * Create a plot object with no {@link AxisPainter}s
	 * @param context the {@link Surface} to draw to
	 */
	public PlotDrawing(Spectrum numbers) {
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
	public PlotDrawing() {
		super(new DrawingRequest());
		this.context = null;
		this.axisPainters = null;
		this.painters = null;
	}
	
	
	/**
	 * Gets the {@link Surface} that plots will be drawn to
	 * @return {@link Surface}
	 */
	public Surface getContext() {
		return context;
	}


	/**
	 * Gets the size of the plot to be drawn
	 * @return the current plot size
	 */
	public Coord<Float> getPlotSize() {
		return plotSize;
	}

	
	public void setAxisPainters(List<AxisPainter> axisPainters) {
		this.axisPainters = axisPainters;
	}
	
	public void setAxisPainters(AxisPainter axisPainter) {
		axisPainters = new ArrayList<AxisPainter>();
		axisPainters.add(axisPainter);
	}
	
	public void setPainters(List<PlotPainter> painters) {
		this.painters = painters;
	}
	
	public void setPainters(PlotPainter painter) {
		painters = new ArrayList<PlotPainter>();
		painters.add(painter);
	}
	
	
	/**
	 * Draws a plot using the given painters
	 */
	@Override
	public void draw() {
		
		final boolean debugMode = false;
		
		if (context == null) return;
		
		
		dataHeights = new ISpectrum(dr.dataWidth, 0.0f);

		context.setLineWidth(getPenWidth(getBaseUnitSize(dr), dr));
		
		context.save();
	
			Coord<Bounds<Float>> axisBounds = getPlotOffsetFromBottomLeft();
			Bounds<Float> availableX = axisBounds.x, availableY = axisBounds.y;
			plotSize = new Coord<Float>(availableX.end - availableX.start + 1, availableY.end - availableY.start + 1); 
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

			if (debugMode) {
				context.rectAt(0, 0, plotSize.x, plotSize.y);
				context.setSource(0x80a40000);
				context.fill();
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
				
				if (debugMode) {
					
					context.setSource(0x8000a400);
					context.rectAt(0, 0, availableX.start, dr.imageHeight);
					context.fill();
					
					context.setSource(0x800000a4);
					context.rectAt(0, availableY.end, dr.imageWidth, dr.imageHeight - availableY.end);
					context.fill();
				
				}
				
				
				
			}
			

		context.restore();

		return;
	}

	/**
	 * Calculates the space required for drawing the axes
	 * @return A coordinate pair defining the x and y space consumed by the axes
	 */
	public Coord<Bounds<Float>> getPlotOffsetFromBottomLeft() {
		PainterData p = new PainterData(context, dr, plotSize, dataHeights);
		return AxisPainter.calcAxisBorders(p, axisPainters);
	}


	public static float getBaseUnitSize(DrawingRequest dr) {
		return dr.imageHeight / 350.0f;
	}


	public static float getDataScale(DrawingRequest dr) {
		
		float datascale;
		if (dr.maxYIntensity == -1){
			return 0;
		} else {
			datascale = dr.maxYIntensity;
		}
		if (dr.viewTransform == ViewTransform.LOG) {
			datascale = (float)Math.log1p(datascale);
		}
		datascale *= 1.15;

		return datascale;
	}	
	
	public static float getDataScale(float maxValue, boolean log, boolean pad) {
		float datascale = maxValue;
		if (pad) {
			if (log) datascale = (float)Math.log1p(datascale);
			datascale *= 1.15;
			if (log) datascale = (float)Math.expm1(datascale);
		}
		return datascale;
	}	
	
	public static float getDataScale(DrawingRequest dr, ReadOnlySpectrum data) {
		
		float datascale;
		if (dr.maxYIntensity == -1){
			datascale = data.max();
		} else {
			datascale = dr.maxYIntensity;
		}
		if (dr.viewTransform == ViewTransform.LOG) {
			datascale = (float)Math.log1p(datascale);
		}
		datascale *= 1.15;

		return datascale;
	}


}
