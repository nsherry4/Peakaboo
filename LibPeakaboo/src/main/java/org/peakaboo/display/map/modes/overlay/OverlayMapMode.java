package org.peakaboo.display.map.modes.overlay;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.MapRenderData;
import org.peakaboo.display.map.MapRenderSettings;
import org.peakaboo.display.map.modes.MapMode;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.visualization.Buffer;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.Surface.CompositeModes;
import org.peakaboo.framework.cyclops.visualization.drawing.ViewTransform;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.FloodMapPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.MapPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.RasterColorMapPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.RasterSpectrumMapPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.SelectionMaskPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.map.painters.axis.LegendCoordsAxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.AxisPainter;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.axis.PaddingAxisPainter;
import org.peakaboo.framework.cyclops.visualization.palette.ColourStopPalette;
import org.peakaboo.framework.cyclops.visualization.palette.PaletteColour;

public class OverlayMapMode extends MapMode {

	public static String MODE_NAME = "Overlay";
	
	private Map<OverlayColour, RasterSpectrumMapPainter> overlayMapPainters;
		
	@Override
	public void draw(Coord<Integer> size, MapRenderData data, MapRenderSettings settings, Surface backend, int spectrumSteps) {
		map.setContext(backend);
		
		OverlayModeData overlayData = (OverlayModeData) data.mapModeData;
		
		backend.rectAt(0, 0, (float)size.x, (float)size.y);
		backend.setSource(settings.getBg());
		backend.fill();
		
		AxisPainter spectrumCoordPainter = null;

		dr.uninterpolatedWidth = settings.filteredDataWidth;
		dr.uninterpolatedHeight = settings.filteredDataHeight;
		dr.dataWidth = settings.filteredDataWidth;
		dr.dataHeight = settings.filteredDataHeight;
		dr.viewTransform = ViewTransform.LINEAR;
		dr.screenOrientation = false;
		dr.drawToVectorSurface = backend.isVectorSurface();
		
		List<Pair<PaletteColour, String>> colours = new ArrayList<>();
		Function<OverlayColour, String> tsFormatter = colour -> overlayData.getData().get(colour).elements
				.stream()
				.map(ITransitionSeries::toString)
				.collect(Collectors.reducing((a, b) -> a + ", " + b)).orElse("");
		
		if (overlayMapPainters == null) {
			overlayMapPainters = new LinkedHashMap<>();
		}
		
		float maxmax = 0f;
		Map<OverlayColour, Spectrum> overlaySpectra = new LinkedHashMap<>();
		for (OverlayColour colour : OverlayColour.values()) {
			Spectrum colourSpectrum = overlayData.getData().get(colour).data;
			float colourMax = 0f;
			if (colourSpectrum != null) {
				overlaySpectra.put(colour, colourSpectrum);
				
				//calculate the max for this colour
				colourMax = colourSpectrum.max();
				//add the colour to a list of colourcode/label pairs
				colours.add(new Pair<>(colour.toColour(), tsFormatter.apply(colour)));
				
				ColourStopPalette palette = new ColourStopPalette(spectrumSteps, colour.toColour());
				
				// create a list of map painters, one for each of the maps we want to show
				RasterSpectrumMapPainter overlayColourMapPainter;
				if (! overlayMapPainters.containsKey(colour)) {
					overlayColourMapPainter = new RasterSpectrumMapPainter(palette, colourSpectrum);
					overlayColourMapPainter.setCompositeMode(CompositeModes.ADD);
					overlayMapPainters.put(colour, overlayColourMapPainter);
				} else {
					overlayColourMapPainter = overlayMapPainters.get(colour);
				}
				overlayColourMapPainter.setData(colourSpectrum);
				overlayColourMapPainter.setPalette(palette);
				
			}
			maxmax = Math.max(maxmax, colourMax);
		}
		dr.maxYIntensity = maxmax;

		
		
		spectrumCoordPainter = new LegendCoordsAxisPainter(

			settings.drawCoord,
			settings.getFg(),
			settings.coordLoXHiY,
			settings.coordHiXHiY,
			settings.coordLoXLoY,
			settings.coordHiXLoY,
			settings.physicalUnits,

			settings.showSpectrum,
			settings.spectrumHeight,

			settings.physicalCoord,
			settings.showScaleBar,
			colours
			
		);

			
		List<AxisPainter> axisPainters = new ArrayList<>();
		super.setupTitleAxisPainters(settings, axisPainters);
		axisPainters.add(new PaddingAxisPainter(0, 0, 10, 0));
		axisPainters.add(getDescriptionPainter(settings));
		axisPainters.add(spectrumCoordPainter);
		map.setAxisPainters(axisPainters);


		
		//Selection Painter
		SelectionMaskPainter selectionPainter = super.getSelectionPainter(
				new PaletteColour(0x80ffffff), 
				settings.selectedPoints, 
				settings.userDataWidth, 
				settings.userDataHeight);

		
		
		if (backend.isVectorSurface()) {
						
			//create new buffer to add the rgby channels in
			Buffer buffer = backend.getImageBuffer(settings.filteredDataWidth, settings.filteredDataHeight);
			PainterData p = new PainterData(buffer, dr, new Coord<Float>((float)dr.dataWidth, (float)dr.dataHeight), null);
			
			/*
			 * Hacky! Go through each colour painter and have it calculate the colour it
			 * would use to draw each pixel, adding them as it goes, until it can create a
			 * list of colours to feed to a RasterColourMapPainter.
			 */
			List<PaletteColour> addedColours = new ArrayList<>();
			for (int i = 0; i < dr.dataWidth * dr.dataHeight; i++) {
				PaletteColour addedColour = new PaletteColour();
				
				for (OverlayColour colour : OverlayColour.values()) {
					if (overlaySpectra.containsKey(colour) && overlayMapPainters.containsKey(colour) ) {
						Spectrum colourSpectrum = overlaySpectra.get(colour);
						RasterSpectrumMapPainter colourPainter = overlayMapPainters.get(colour);
						double maxIntensity = colourPainter.calcMaxIntensity(p);
						if (dr.viewTransform == ViewTransform.LOG) {
							//intensity will already have been log'd, we just have to log the max
							maxIntensity = Math.log1p(maxIntensity);
						}
						addedColour = addedColour.add(colourPainter.getColourFromRules(colourSpectrum.get(i), maxIntensity));
					}
				}
				addedColours.add(addedColour);
			}
			
			
			//get the pixels from the buffer as PaletteColour objects and pass them to a RasterColourMapPainter
			RasterColorMapPainter addedColoursMapPainter = new RasterColorMapPainter();
			addedColoursMapPainter.setPixels(addedColours);
			
			//set up the list of painters
			List<MapPainter> painters = new ArrayList<>();
			painters.add(new FloodMapPainter(new PaletteColour(0xff000000))); //background
			painters.add(addedColoursMapPainter);
			painters.add(selectionPainter);
			map.setPainters(painters);
			
			//draw to the real backend
			map.setContext(backend);
			map.draw();
			
		} else {
			
			List<MapPainter> painters = new ArrayList<>();
			painters.add(new FloodMapPainter(new PaletteColour(0xff000000))); //background
			for (OverlayColour colour : OverlayColour.values()) {
				if (overlaySpectra.containsKey(colour) && overlayMapPainters.containsKey(colour) ) {
					RasterSpectrumMapPainter colourPainter = overlayMapPainters.get(colour);
					painters.add(colourPainter);
				}
			}
			painters.add(selectionPainter);
			map.setPainters(painters);
			
			map.setContext(backend);
			map.draw();
		}
		
	}

	
	@Override
	public void invalidate() {
		map.needsMapRepaint();
		if (overlayMapPainters != null) {
			for (RasterSpectrumMapPainter painter : overlayMapPainters.values()) {
				painter.clearBuffer();
			}
		}
	}

	@Override
	public String mapModeName() {
		return MODE_NAME;
	}

}
