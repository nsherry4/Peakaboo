package org.peakaboo.display.plot;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.common.PeakabooConfiguration;
import org.peakaboo.common.PeakabooConfiguration.MemorySize;
import org.peakaboo.common.PeakabooLog;
import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.curve.fitting.FittingResultSet;
import org.peakaboo.display.plot.painters.FittingLabel;
import org.peakaboo.display.plot.painters.FittingMarkersPainter;
import org.peakaboo.display.plot.painters.FittingPainter;
import org.peakaboo.display.plot.painters.FittingSumPainter;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.SpectrumCalculations;
import org.peakaboo.framework.cyclops.visualization.Buffer;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.SurfaceType;
import org.peakaboo.framework.cyclops.visualization.drawing.DrawingRequest;
import org.peakaboo.framework.cyclops.visualization.drawing.ViewTransform;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.LineAxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.TitleAxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.PlotDrawing;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.PlotPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.SpectrumPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.GridlinePainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.TickMarkAxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.TickMarkAxisPainter.TickFormatter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.DataLabelPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.OriginalDataPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.PlotPalette;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.PrimaryPlotPainter;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

public class Plotter {


	
	private int spectrumSize = 2048;
	
	private Buffer buffer;
	private Coord<Integer> bufferSize;
	private PlotDrawing plotDrawing;
	
	public Plotter() {
	}
	
	public PlotDrawing draw(PlotData data, PlotSettings settings, Surface context, Coord<Integer> size) {
		
		if (settings == null) {
			settings = new PlotSettings();
		}
		

		if (data.filtered == null) {
			PeakabooLog.get().log(Level.WARNING, "Could not draw plot, data (filtered) was null");
			plotDrawing = null;
			return null;
		};
		spectrumSize = data.filtered.size();
		
		
		
		
		
		//buffer space in MB
		boolean doBuffer = true;
		int bufferSpace = (int)((size.x * 1.2f * size.y * 1.2f * 4) / 1024f / 1024f);
		if (bufferSpace > 10 && PeakabooConfiguration.memorySize == MemorySize.TINY) {
			doBuffer = false;
		}
		if (bufferSpace > 20 && PeakabooConfiguration.memorySize == MemorySize.SMALL) {
			doBuffer = false;
		}
		if (bufferSpace > 40 && PeakabooConfiguration.memorySize == MemorySize.MEDIUM) {
			doBuffer = false;
		}
		if (bufferSpace > 250 && PeakabooConfiguration.memorySize == MemorySize.LARGE) {
			doBuffer = false;
		}
		
		 
		if (context.getSurfaceType() != SurfaceType.RASTER) {
			//We can't do raster-based buffering if the drawing target is vector/pdf
			//so just draw directly to the surface
			drawToBuffer(data, settings, context, size);
		} else if (doBuffer) {
			if (buffer == null || plotDrawing == null || bufferSize == null || bufferSize.x < size.x || bufferSize.y < size.y) {
				buffer = context.getImageBuffer((int)(size.x*1.2f), (int)(size.y*1.2f));
				bufferSize = size;
				drawToBuffer(data, settings, buffer, size);
			} else if (!bufferSize.equals(size)) {
				//buffer exists, but size has changed, requiring redraw.
				drawToBuffer(data, settings, buffer, size);
			}
			
			context.rectAt(0, 0, size.x, size.y);
			context.clip();
			context.compose(buffer, 0, 0, 1f);
		} else {
			buffer = null;
			bufferSize = null;
			drawToBuffer(data, settings, context, size);
		}
		
		return plotDrawing;

		
	}
	
	public PlotDrawing drawToBuffer(PlotData data, PlotSettings settings, Surface context, Coord<Integer> size) {
		

		DrawingRequest dr = new DrawingRequest();
		
		
		//white background
		context.rectAt(0, 0, (float)size.x, (float)size.y);
		context.setSource(new PaletteColour(0xffffffff));
		context.fill();

		

		////////////////////////////////////////////////////////////////////
		// Colour Selections
		////////////////////////////////////////////////////////////////////
		PlotPalette fittedPalette = new PlotPalette();
		PlotPalette proposedPalette = new PlotPalette();
		PlotPalette selectedPalette = new PlotPalette();
//		Color fitting, fittingStroke, fittingSum, fittingLabel, fittingLabelBg;
//		Color proposed, proposedStroke, proposedSum, proposedLabel, proposedLabelBg;
//		Color selected, selectedStroke, selectedLabel, selectedLabelBg;


		
		// Colour/Monochrome colours for curve fittings
		if (settings.monochrome) {
			fittedPalette.fitFill = new PaletteColour(0x50000000);
			fittedPalette.fitStroke = new PaletteColour(0x80000000);
			fittedPalette.sumStroke = new PaletteColour(0xD0000000);
			fittedPalette.labelText = new PaletteColour(0xFF000000);
			fittedPalette.labelBackground = new PaletteColour(0xffffffff);
			fittedPalette.labelStroke = fittedPalette.labelText;
			fittedPalette.markings = fittedPalette.fitStroke;
		} else {
			fittedPalette.fitFill = new PaletteColour(0x50000000);
			fittedPalette.fitStroke = new PaletteColour(0x80000000);
			fittedPalette.sumStroke = new PaletteColour(0xD0000000);
			fittedPalette.labelText = fittedPalette.fitStroke;
			fittedPalette.labelBackground = new PaletteColour(0xffffffff);
			fittedPalette.labelStroke = fittedPalette.labelText;
			fittedPalette.markings = fittedPalette.fitStroke;
		}
		
		
		if (settings.monochrome)
		{
			proposedPalette.fitFill = new PaletteColour(0x50ffffff);
			proposedPalette.fitStroke = new PaletteColour(0x80ffffff);
			proposedPalette.sumStroke = new PaletteColour(0xD0ffffff);
			proposedPalette.labelText = new PaletteColour(0xFF777777);
			proposedPalette.labelBackground = new PaletteColour(0xffffffff);
			proposedPalette.labelStroke = proposedPalette.labelText;
			proposedPalette.markings = proposedPalette.fitStroke;
		}
		else
		{
			proposedPalette.fitFill = new PaletteColour(0xA0D32F2F);
			proposedPalette.fitStroke = new PaletteColour(0xA0B71C1C);
			proposedPalette.sumStroke = new PaletteColour(0xD0B71C1C);
			proposedPalette.labelText = new PaletteColour(0xffffffff);
			proposedPalette.labelBackground = proposedPalette.fitStroke;
			proposedPalette.labelStroke = proposedPalette.labelBackground;
			proposedPalette.markings = proposedPalette.fitStroke;
		}
		
		// Colour/Monochrome colours for highlighted/selected fittings
		if (settings.monochrome)
		{
			selectedPalette.fitFill = new PaletteColour(0x50ffffff);
			selectedPalette.fitStroke = new PaletteColour(0x80ffffff);
			selectedPalette.sumStroke = new PaletteColour(0xFF777777);
			selectedPalette.labelText = new PaletteColour(0xffffffff);
			selectedPalette.labelBackground = new PaletteColour(0x80000000);
			selectedPalette.labelStroke = new PaletteColour(0xA0000000);
			selectedPalette.markings = selectedPalette.fitStroke;
		}
		else
		{
			selectedPalette.fitFill = new PaletteColour(0x800288D1);
			selectedPalette.fitStroke = new PaletteColour(0xff01579B);
			selectedPalette.sumStroke = new PaletteColour(0xff01579B);
			selectedPalette.labelText = new PaletteColour(0xffffffff);
			selectedPalette.labelBackground = new PaletteColour(0xA001579B);
			selectedPalette.labelStroke = selectedPalette.fitStroke;
			selectedPalette.markings = selectedPalette.fitStroke;
		}

		
		
		
		
		////////////////////////////////////////////////////////////////////
		// Plot Painters
		////////////////////////////////////////////////////////////////////

		//if the filtered data somehow becomes taller than the maximum value from the raw data, we don't want to clip it.
		//but if the fitlered data gets weaker, we still want to scale it to the original data, so that its shrinking is obvious
		float maxIntensity = Math.max(data.dataset.getAnalysis().maximumIntensity(), data.filtered.max());
		
		//when not using the consistent scale, scale each spectra against itself
		if (!data.consistentScale) {
			maxIntensity = data.filtered.max();
		}
		
		
		
		dr.imageWidth = (float) size.x;
		dr.imageHeight = (float) size.y;
		dr.viewTransform = settings.logTransform ? ViewTransform.LOG : ViewTransform.LINEAR;
		dr.unitSize = (data.calibration.getMaxEnergy() - data.calibration.getMinEnergy()) / (float)data.calibration.getDataWidth();
		dr.drawToVectorSurface = context.isVectorSurface();
		

		
		List<PlotPainter> plotPainters = new ArrayList<PlotPainter>();
		
		boolean log = dr.viewTransform == ViewTransform.LOG;
		TickFormatter tickRight = new TickFormatter(0.0f, maxIntensity).withLog(log).withRotate(true);
		TickFormatter tickBottom = new TickFormatter(data.calibration.getMinEnergy(), data.calibration.getMaxEnergy()).withRotate(false);
		TickFormatter tickTop = null;
		TickFormatter tickLeft = new TickFormatter(0.0f, maxIntensity).withLog(log).withRotate(true);
		
		
		//draw horizontal grid lines. We do this right up front so they're behind everything else
		plotPainters.add(new GridlinePainter(tickLeft));


		// draw the filtered data
		plotPainters.add(new PrimaryPlotPainter(data.filtered, settings.monochrome));

		
		// draw the original data
		if (data.raw != null && settings.backgroundShowOriginal) {
			ReadOnlySpectrum originalData = data.raw;
			plotPainters.add(new OriginalDataPainter(originalData, settings.monochrome));
		}
		
		
		//Filter previews
		PlotPainter filterPainter;
		for (Filter f : data.filters) {
			if (!f.isPreviewOnly()) { continue; }
			
			filterPainter = new SpectrumPainter(data.deltas.get(f)) {

				@Override
				public void drawElement(PainterData p)
				{
					traceData(p);
					p.context.setSource(0.36f, 0.21f, 0.4f);
					p.context.stroke();

				}
			};
			plotPainters.add(filterPainter);
			
		}
		
		
		////////////////////////////////////////////
		// Draw Curve Fitting
		////////////////////////////////////////////
		
		//Colour palettes for fittings
		List<FittingLabel> fitLabels = new ArrayList<>();
		if (data.selectionResults != null) {
			for (FittingResult fit : data.selectionResults.getFits()) {
				if (data.highlightedTransitionSeries.contains(fit.getTransitionSeries())) {
					fitLabels.add(new FittingLabel(fit, selectedPalette, data.calibration, data.annotations.get(fit.getTransitionSeries()), settings.showElementFitIntensities));		
				} else {
					fitLabels.add(new FittingLabel(fit, fittedPalette, data.calibration, data.annotations.get(fit.getTransitionSeries()), settings.showElementFitIntensities));
				}
				
			}
		}
		
		
		//Markings
		if (data.selectionResults != null) {
			if (settings.showElementFitMarkers) {
				plotPainters.add(new FittingMarkersPainter(data.selectionResults.getParameters(), fitLabels, data.escape));
			}
		}
		
		
		//plot
		if (data.selectionResults != null) {
			if (settings.showIndividualFittings)
			{
				//draw the selected & highlighted results here, since we always draw the highlight
				//on top of the black curve to be consistent
				plotPainters.add(new FittingPainter(data.selectionResults, fittedPalette));
				plotPainters.add(new FittingSumPainter(data.selectionResults.getTotalFit(), fittedPalette, false));
			}
			else
			{			
				plotPainters.add(new FittingSumPainter(data.selectionResults.getTotalFit(), fittedPalette, true));
			}
			
			//highlighted fittings
			if (data.selectionResults != null && data.selectionResults.size() > 0) {
				FittingResultSet highlightedResults = data.selectionResults.subsetIntersect(data.highlightedTransitionSeries);
				plotPainters.add(new FittingPainter(highlightedResults, selectedPalette));
			}
		}
		
		
		//draw curve fitting for proposed fittings
		if (data.proposedTransitionSeries.size() > 0)
		{
			if (settings.showIndividualFittings) {
				plotPainters.add(new FittingPainter(data.proposedResults, proposedPalette));
			} else {
				plotPainters.add(new FittingSumPainter(data.proposedResults.getTotalFit(), proposedPalette, true));
			}

			plotPainters.add(

				new FittingSumPainter(SpectrumCalculations.addLists(
						data.proposedResults.getTotalFit(),
						data.selectionResults.getTotalFit()), proposedPalette, false)

			);
		}
		

		
		
		
		//Titles
		if (data.proposedResults != null) {
			for (FittingResult fit : data.proposedResults.getFits()) {
				fitLabels.add(new FittingLabel(fit, proposedPalette, data.calibration, data.annotations.get(fit.getTransitionSeries()), settings.showElementFitIntensities));
			}
		}
		
		if (data.selectionResults != null) {
			plotPainters.add(new DataLabelPainter(fitLabels)
			);
		}
		
		

		
		////////////////////////////////////////////////////////////////////
		// Axis Painters
		////////////////////////////////////////////////////////////////////

		//if (axisPainters == null)
		//{
		List<AxisPainter> axisPainters = new ArrayList<AxisPainter>();



		
		axisPainters.add(new TitleAxisPainter(TitleAxisPainter.SCALE_TITLE, "Relative Intensity", null, null, "Energy (keV)"));
		
		axisPainters.add(new TickMarkAxisPainter(tickRight, tickBottom, tickTop, tickLeft));
		axisPainters.add(new LineAxisPainter(true, true, false, true));


		dr.maxYIntensity = maxIntensity;
		dr.dataWidth = data.calibration.getDataWidth();
		
		
		plotDrawing = new PlotDrawing(context, dr, plotPainters, axisPainters);
		plotDrawing.draw();
				
		return plotDrawing;
		
	}
	
	
	public PlotDrawing getPlot() {
		return plotDrawing;
	}
	

	public int getChannel(int x) {
		if (plotDrawing == null) return -1;

		Coord<Bounds<Float>> axesSize;
		int channel;

		// Plot p = new Plot(this.toyContext, model.dr);
		axesSize = plotDrawing.getPlotOffsetFromBottomLeft();

		float plotWidth = axesSize.x.end - axesSize.x.start; // width - axesSize.x;
		// x -= axesSize.x;
		x -= axesSize.x.start;

		if (x < 0) return -1;

		channel = (int) ((x / plotWidth) * spectrumSize);
		return channel;

	}
	
	
	public void setNeedsRedraw() {
		buffer = null;
		plotDrawing = null;
	}
	
}
