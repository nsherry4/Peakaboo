package org.peakaboo.display.plot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.curvefit.curve.fitting.FittingResult;
import org.peakaboo.curvefit.curve.fitting.FittingResultSet;
import org.peakaboo.display.Display;
import org.peakaboo.display.plot.painters.FittingLabel;
import org.peakaboo.display.plot.painters.FittingMarkersPainter;
import org.peakaboo.display.plot.painters.FittingPainter;
import org.peakaboo.display.plot.painters.FittingSumPainter;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.spectrum.ReadOnlySpectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;
import org.peakaboo.framework.cyclops.visualization.Buffer;
import org.peakaboo.framework.cyclops.visualization.ManagedBuffer;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.Surface.Dash;
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
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.GridlinePainter.Config;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.GridlinePainter.Orientation;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.RangeTickFormatter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.TickFormatter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.TickMarkAxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.AreaPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.DataLabelPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.OriginalDataPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.plot.PlotPalette;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

public class Plotter {


	
	private int spectrumSize = 2048;
	ManagedBuffer bufferer = new ManagedBuffer(Display.OVERSIZE);
	
	private Coord<Integer> lastSize;
	private PlotDrawing plotDrawing;

	
	public PlotDrawing draw(PlotData data, PlotSettings settings, Surface context, Coord<Integer> size) {
		
		if (settings == null) {
			settings = new PlotSettings();
		}
		

		if (data.filtered == null) {
			PeakabooLog.get().log(Level.WARNING, "Could not draw plot, data (filtered) was null");
			plotDrawing = null;
			return null;
		}
		spectrumSize = data.filtered.size();

		
		
		//Should be use a buffer, or are we too tight on memory?
		boolean doBuffer = Display.useBuffer(size);
		 
		if (context.getSurfaceDescriptor().isVector()) {
			//We can't do raster-based buffering if the drawing target is vector
			//so just draw directly to the surface
			drawToSurface(data, settings, context, size);
		} else if (doBuffer) {
			
			Buffer buffer = bufferer.get(context, size.x, size.y);
			boolean needsRedraw = buffer == null || lastSize == null || !lastSize.equals(size);
			//if there is no cached buffer meeting our size requirements, create it and draw to it
			if (needsRedraw) {
				if (buffer == null) {
					buffer = bufferer.create(context);
				}
				drawToSurface(data, settings, buffer, size);
				lastSize = new Coord<>(size); 
			}
					
			context.rectAt(0, 0, size.x, size.y);
			context.clip();
			context.compose(buffer, 0, 0, 1f);
		} else {
			lastSize = null;
			drawToSurface(data, settings, context, size);
		}
		
		return plotDrawing;

		
	}
	
	
	public PlotDrawing drawToSurface(PlotData data, PlotSettings settings, Surface context, Coord<Integer> size) {

		////////////////////////////////////////////////////////////////////
		// Colour Selections
		////////////////////////////////////////////////////////////////////
		PlotPalette fittedPalette = getFittedPalette(settings.monochrome);
		PlotPalette proposedPalette = getProposedPalette(settings.monochrome);
		PlotPalette selectedPalette = getSelectedPalette(settings.monochrome);

		
				
		////////////////////////////////////////////////////////////////////
		// Common Values Setup
		////////////////////////////////////////////////////////////////////
		//The max signal intensity is different than the plot's maximum displayable value, since it leaves a bit of padding room at the top
		float plotMaxSignal = getMaxIntensity(data);
		float plotMaxValue = PlotDrawing.getDataScale(plotMaxSignal, settings.logTransform, true);
		
		TickFormatter tickRight = new RangeTickFormatter(0.0f, plotMaxValue, plotMaxSignal)
				.withLog(settings.logTransform)
				.withRotate(true)
				.withPad(true);
		TickFormatter tickBottom = new RangeTickFormatter(data.calibration.getMinEnergy(), data.calibration.getMaxEnergy()).withRotate(false);
		TickFormatter tickTop = null;
		TickFormatter tickLeft = new RangeTickFormatter(0.0f, plotMaxValue, plotMaxSignal)
				.withLog(settings.logTransform)
				.withRotate(true)
				.withPad(true);
		
		
		
		////////////////////////////////////////////////////////////////////
		// Plot Painters
		////////////////////////////////////////////////////////////////////
		List<PlotPainter> plotPainters = new ArrayList<>();
		
		//draw grid lines. We do this right up front so they're behind everything else
		plotPainters.add(new GridlinePainter(new Config(Orientation.HORIZONTAL, tickLeft)));
		var vLineColour = new PaletteColour(0x08000000);
		plotPainters.add(new GridlinePainter(new Config(Orientation.VERTICAL, tickBottom, vLineColour, vLineColour, new Dash(new float[] {3}, 0))));
		
		

		// draw the filtered data
		plotPainters.add(getPlotPainter(data.filtered, settings.monochrome));

		
		// draw the original data
		if (data.raw != null && settings.backgroundShowOriginal) {
			ReadOnlySpectrum originalData = data.raw;
			plotPainters.add(new OriginalDataPainter(originalData, settings.monochrome));
		}
		
		
		//Filter previews
		for (Filter f : data.filters) {
			if (!f.isPreviewOnly()) { continue; }
			plotPainters.add(createFilterPreviewPainter(data, f));
		}
		
		
		////////////////////////////////////////////
		// Curve Fitting Plot Painters
		////////////////////////////////////////////
		
		//Labels describing fits
		List<FittingLabel> fitLabels = createFittingLabels(data, settings.showElementFitIntensities, selectedPalette, fittedPalette, proposedPalette);
		
		
		//Draw fits
		plotPainters.addAll(getFittingPainters(data, settings, fittedPalette, selectedPalette));

		//Draw proposed fits
		plotPainters.addAll(getProposedFittingPainters(data, settings, proposedPalette));
		
		//Element transition marker lines
		if (data.selectionResults != null && settings.showElementFitMarkers) {
			plotPainters.add(new FittingMarkersPainter(data.selectionResults.getParameters(), fitLabels));
		}
		
		//Fitting Labels/Titles
		if (data.selectionResults != null) {
			plotPainters.add(new DataLabelPainter(fitLabels));
		}

		
		
		////////////////////////////////////////////////////////////////////
		// Axis Painters
		////////////////////////////////////////////////////////////////////

		List<AxisPainter> axisPainters = new ArrayList<>();		
		axisPainters.add(new TitleAxisPainter(TitleAxisPainter.SCALE_TITLE, "Relative Intensity", null, settings.title, "Energy (keV)"));
		axisPainters.add(new TickMarkAxisPainter(tickRight, tickBottom, tickTop, tickLeft));
		axisPainters.add(new LineAxisPainter(true, true, false, true));

		
		
		////////////////////////////////////////////////////////////////////
		// Draw
		////////////////////////////////////////////////////////////////////
		
		//Create the DrawingRequest object
		DrawingRequest dr = createDrawingRequest(data, size, settings, context);
		
		clearSurface(context, size);
		plotDrawing = new PlotDrawing(context, dr, plotPainters, axisPainters);
		plotDrawing.draw();
				
		return plotDrawing;
		
	}
	
	
	private Collection<? extends PlotPainter> getFittingPainters(
			PlotData data, 
			PlotSettings settings,
			PlotPalette fittedPalette, 
			PlotPalette selectedPalette) {

		List<PlotPainter> plotPainters = new ArrayList<>();
		
		if (data.selectionResults != null) {
			
			if (settings.showIndividualFittings) {
				//draw the selected & highlighted results here, since we always draw the highlight
				//on top of the black curve to be consistent
				plotPainters.add(new FittingPainter(data.selectionResults, fittedPalette));
				plotPainters.add(new FittingSumPainter(data.selectionResults.getTotalFit(), fittedPalette, false));
			} else {			
				plotPainters.add(new FittingSumPainter(data.selectionResults.getTotalFit(), fittedPalette, true));
			}
			
			//highlighted fittings
			FittingResultSet highlightedResults = data.selectionResults.subsetIntersect(data.highlightedTransitionSeries);
			if (!highlightedResults.isEmpty()) {
				plotPainters.add(new FittingPainter(highlightedResults, selectedPalette));
			}
		}
		
		return plotPainters;
		
	}

	private List<PlotPainter> getProposedFittingPainters(
			PlotData data, 
			PlotSettings settings,
			PlotPalette proposedPalette) {

		List<PlotPainter> plotPainters = new ArrayList<>();
		if (!data.proposedTransitionSeries.isEmpty()) {
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
		
		return plotPainters;
		
	}

	private List<FittingLabel> createFittingLabels(
			PlotData data, 
			boolean showElementFitIntensities,
			PlotPalette selectedPalette, 
			PlotPalette fittedPalette,
			PlotPalette proposedPalette) {
		
		List<FittingLabel> fitLabels = new ArrayList<>();
		if (data.selectionResults != null) {
			for (FittingResult fit : data.selectionResults.getFits()) {
				if (data.highlightedTransitionSeries.contains(fit.getTransitionSeries())) {
					fitLabels.add(new FittingLabel(fit, selectedPalette, data.calibration, data.annotations.get(fit.getTransitionSeries()), showElementFitIntensities));		
				} else {
					fitLabels.add(new FittingLabel(fit, fittedPalette, data.calibration, data.annotations.get(fit.getTransitionSeries()), showElementFitIntensities));
				}
				
			}
		}
		if (data.proposedResults != null) {
			for (FittingResult fit : data.proposedResults.getFits()) {
				fitLabels.add(new FittingLabel(fit, proposedPalette, data.calibration, data.annotations.get(fit.getTransitionSeries()), showElementFitIntensities));
			}
		}
		return fitLabels;
	}

	private PlotPainter createFilterPreviewPainter(PlotData data, Filter f) {
		return new SpectrumPainter(data.deltas.get(f)) {

			@Override
			public void drawElement(PainterData p)
			{
				traceData(p);
				p.context.setSource(0x7f5C3666);
				p.context.fillPreserve();
				p.context.setSource(0xff5C3666);
				p.context.stroke();

			}
		};
	}

	private DrawingRequest createDrawingRequest(
			PlotData data, 
			Coord<Integer> size, 
			PlotSettings settings,
			Surface context) {
		DrawingRequest dr = new DrawingRequest();
		dr.imageWidth = (float) size.x;
		dr.imageHeight = (float) size.y;
		dr.viewTransform = settings.logTransform ? ViewTransform.LOG : ViewTransform.LINEAR;
		dr.unitSize = (data.calibration.getMaxEnergy() - data.calibration.getMinEnergy()) / (float)data.calibration.getDataWidth();
		dr.drawToVectorSurface = context.isVectorSurface();
		dr.dataWidth = data.calibration.getDataWidth();
		dr.maxYIntensity = getMaxIntensity(data);
		return dr;
	}

	private float getMaxIntensity(PlotData data) {
		//if the filtered data somehow becomes taller than the maximum value from the raw data, we don't want to clip it.
		//but if the fitlered data gets weaker, we still want to scale it to the original data, so that its shrinking is obvious
		float maxIntensity = Math.max(data.dataset.getAnalysis().maximumIntensity(), data.filtered.max());
		
		//when not using the consistent scale, scale each spectra against itself
		if (!data.consistentScale) {
			maxIntensity = data.filtered.max();
		}
		
		return maxIntensity;
	}

	private void clearSurface(Surface context, Coord<Integer> size) {
		context.rectAt(0, 0, (float)size.x, (float)size.y);
		context.setSource(new PaletteColour(0xffffffff));
		context.fill();
	}
	
	private AreaPainter getPlotPainter(ReadOnlySpectrum filtered, boolean monochrome) {
		PaletteColour fill = new PaletteColour(monochrome ? 0xff606060 : 0xff26a269);
		PaletteColour stroke = new PaletteColour(monochrome ? 0xff202020 : 0xff1e7e52);
		return new AreaPainter(filtered, fill, stroke);
	}

	private PlotPalette getSelectedPalette(boolean monochrome) {
		PlotPalette palette = new PlotPalette();
		// Colour/Monochrome colours for highlighted/selected fittings
		if (monochrome)
		{
			palette.fitFill = new PaletteColour(0x50ffffff);
			palette.fitStroke = new PaletteColour(0x80ffffff);
			palette.sumStroke = new PaletteColour(0xFF777777);
			palette.labelText = new PaletteColour(0xffffffff);
			palette.labelBackground = new PaletteColour(0x80000000);
			palette.labelStroke = new PaletteColour(0xA0000000);
			palette.markings = palette.fitStroke;
		}
		else
		{
			palette.fitFill = new PaletteColour(0x801c71d8);
			palette.fitStroke = new PaletteColour(0xff1a5fb4);
			palette.sumStroke = new PaletteColour(0xff1a5fb4);
			palette.labelText = new PaletteColour(0xffffffff);
			palette.labelBackground = new PaletteColour(0xA01c71d8);
			palette.labelStroke = palette.fitStroke;
			palette.markings = palette.fitStroke;
		}
		return palette;
	}

	private PlotPalette getProposedPalette(boolean monochrome) {
		PlotPalette palette = new PlotPalette();
		if (monochrome)
		{
			palette.fitFill = new PaletteColour(0x50ffffff);
			palette.fitStroke = new PaletteColour(0x80ffffff);
			palette.sumStroke = new PaletteColour(0xD0ffffff);
			palette.labelText = new PaletteColour(0xFF777777);
			palette.labelBackground = new PaletteColour(0xffffffff);
			palette.labelStroke = palette.labelText;
			palette.markings = palette.fitStroke;
		}
		else
		{
			palette.fitFill = new PaletteColour(0xA09141ac);
			palette.fitStroke = new PaletteColour(0xA0613583);
			palette.sumStroke = new PaletteColour(0xD0613583);
			palette.labelText = new PaletteColour(0xffffffff);
			palette.labelBackground = palette.fitStroke;
			palette.labelStroke = palette.labelBackground;
			palette.markings = palette.fitStroke;
		}
		return palette;
	}

	private PlotPalette getFittedPalette(boolean monochrome) {
		PlotPalette palette = new PlotPalette();
		if (monochrome) {
			palette.fitFill = new PaletteColour(0x50000000);
			palette.fitStroke = new PaletteColour(0x80000000);
			palette.sumStroke = new PaletteColour(0xD0000000);
			palette.labelText = new PaletteColour(0xFF000000);
			palette.labelBackground = new PaletteColour(0xffffffff);
			palette.labelStroke = palette.labelText;
			palette.markings = palette.fitStroke;
		} else {
			palette.fitFill = new PaletteColour(0x50000000);
			palette.fitStroke = new PaletteColour(0xA0000000);
			palette.sumStroke = new PaletteColour(0xD0000000);
			palette.labelText = palette.fitStroke;
			palette.labelBackground = new PaletteColour(0xffffffff);
			palette.labelStroke = palette.labelText;
			palette.markings = palette.fitStroke;
		}
		return palette;
	}

	public PlotDrawing getPlot() {
		return plotDrawing;
	}
	

	public int getChannel(int x) {
		if (plotDrawing == null) return -1;

		Coord<Bounds<Float>> axesSize;
		int channel;

		axesSize = plotDrawing.getPlotOffsetFromBottomLeft();

		float plotWidth = axesSize.x.end - axesSize.x.start;
		x -= axesSize.x.start;

		if (x < 0) return -1;

		channel = (int) ((x / plotWidth) * spectrumSize);
		return channel;

	}
	
	
	public void invalidate() {
		lastSize = null;
		plotDrawing = null;
	}
	
}
