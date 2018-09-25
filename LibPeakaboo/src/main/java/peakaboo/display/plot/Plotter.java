package peakaboo.display.plot;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import peakaboo.common.PeakabooConfiguration;
import peakaboo.common.PeakabooLog;
import peakaboo.controller.plotter.view.PlotData;
import peakaboo.controller.plotter.view.PlotSettings;
import peakaboo.curvefit.curve.fitting.FittingResult;
import peakaboo.curvefit.curve.fitting.FittingResultSet;
import peakaboo.display.plot.painters.FittingMarkersPainter;
import peakaboo.display.plot.painters.FittingPainter;
import peakaboo.display.plot.painters.FittingSumPainter;
import peakaboo.display.plot.painters.FittingLabel;
import peakaboo.display.plot.painters.FittingTitlePainter;
import peakaboo.display.plot.painters.FittingLabel.PlotPalette;
import peakaboo.filter.model.Filter;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.ReadOnlySpectrum;
import scitypes.SpectrumCalculations;
import scitypes.visualization.SaveableSurface;
import scitypes.visualization.Surface;
import scitypes.visualization.SurfaceType;
import scitypes.visualization.drawing.DrawingRequest;
import scitypes.visualization.drawing.ViewTransform;
import scitypes.visualization.drawing.painters.PainterData;
import scitypes.visualization.drawing.painters.axis.AxisPainter;
import scitypes.visualization.drawing.painters.axis.LineAxisPainter;
import scitypes.visualization.drawing.painters.axis.TitleAxisPainter;
import scitypes.visualization.drawing.plot.PlotDrawing;
import scitypes.visualization.drawing.plot.painters.PlotPainter;
import scitypes.visualization.drawing.plot.painters.SpectrumPainter;
import scitypes.visualization.drawing.plot.painters.axis.GridlinePainter;
import scitypes.visualization.drawing.plot.painters.axis.TickMarkAxisPainter;
import scitypes.visualization.drawing.plot.painters.plot.OriginalDataPainter;
import scitypes.visualization.drawing.plot.painters.plot.PrimaryPlotPainter;
import scitypes.visualization.palette.PaletteColour;
import scitypes.visualization.template.Rectangle;

public class Plotter {


	public PlotDrawing draw(PlotData data, PlotSettings settings, Surface context, Coord<Integer> size) {
		
		if (settings == null) {
			settings = new PlotSettings();
		}
		
		DrawingRequest dr = new DrawingRequest();
		

		if (data.filtered == null) {
			PeakabooLog.get().log(Level.WARNING, "Could not draw plot, data (filtered) was null");
			return null;
		};
		
		
		
		
		
		//white background
		context.addShape(new Rectangle(0, 0, (float)size.x, (float)size.y));
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
		

		// if axes are shown, also draw horizontal grid lines
		List<PlotPainter> plotPainters = new ArrayList<PlotPainter>();
		plotPainters.add(new GridlinePainter(new Bounds<Float>(
			0.0f,
			maxIntensity)));


		// draw the filtered data
		plotPainters.add(new PrimaryPlotPainter(data.filtered, settings.monochrome));

		
		// draw the original data
		if (data.raw != null && settings.backgroundShowOriginal) {
			ReadOnlySpectrum originalData = data.raw;
			plotPainters.add(new OriginalDataPainter(originalData, settings.monochrome));
		}
		
		
		// get any painters that the filters might want to add to the mix
		// note that this style is now deprecated, and should be removed
		// TODO: Remove in Peakaboo 6 
		PlotPainter filterPainter;
		for (Filter f : data.filters)
		{
			Object painterObject = f.getPainter();
			if (painterObject instanceof PlotPainter) {
				filterPainter = (PlotPainter) painterObject;
				
				if (filterPainter != null && f.isEnabled()) {
					filterPainter.setSourceName(f.getFilterName());
					plotPainters.add(filterPainter);
				}
			}
		}

		//New way of drawing filter previews
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
					fitLabels.add(new FittingLabel(fit, selectedPalette, data.annotations.get(fit.getTransitionSeries())));		
				} else {
					fitLabels.add(new FittingLabel(fit, fittedPalette, data.annotations.get(fit.getTransitionSeries())));
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
				fitLabels.add(new FittingLabel(fit, proposedPalette, data.annotations.get(fit.getTransitionSeries())));
			}
		}
		
		if (data.selectionResults != null) {
			plotPainters.add(new FittingTitlePainter(
					data.selectionResults.getParameters().getCalibration(),
					fitLabels,
					settings.showElementFitIntensities
				)
			);
		}
		
		
		

		
		
		
		

		
		////////////////////////////////////////////////////////////////////
		// Axis Painters
		////////////////////////////////////////////////////////////////////

		//if (axisPainters == null)
		//{
		List<AxisPainter> axisPainters = new ArrayList<AxisPainter>();


		axisPainters.add(new TitleAxisPainter(1.0f, "Relative Intensity", null, null, "Energy (keV)"));
		axisPainters.add(new TickMarkAxisPainter(
			new Bounds<Float>(0.0f, maxIntensity),
			new Bounds<Float>(data.calibration.getMinEnergy(), data.calibration.getMaxEnergy()),
			null,
			new Bounds<Float>(0.0f, maxIntensity),
			dr.viewTransform == ViewTransform.LOG,
			dr.viewTransform == ViewTransform.LOG));
		axisPainters.add(new LineAxisPainter(true, true, false, true));


		dr.maxYIntensity = maxIntensity;
		dr.dataWidth = data.calibration.getDataWidth();
		
		
		PlotDrawing plot = new PlotDrawing(context, dr, plotPainters, axisPainters);
		plot.draw();
				
		return plot;

		
	}
	
	public void write(PlotData data, PlotSettings settings, SurfaceType type, Coord<Integer> size, OutputStream out) throws IOException {
		
		if (PeakabooConfiguration.surfaceFactory == null) {
			throw new RuntimeException("No Drawing Surface Factory Specified");
		}
		SaveableSurface s = PeakabooConfiguration.surfaceFactory.createSaveableSurface(type, (int)size.x, (int)size.y);
		this.draw(data, settings, s, size);
		s.write(out);
		
	}
	
	public void write(PlotData data, PlotSettings settings, SurfaceType type, Coord<Integer> size, Path destination) throws IOException {
		
		OutputStream stream = Files.newOutputStream(destination, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
		this.write(data, settings, type, size, stream);
		stream.flush();
		stream.close();
		
	}
	
}
