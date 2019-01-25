package org.peakaboo.display.map;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.peakaboo.display.map.modes.MapDisplayMode;
import org.peakaboo.display.map.modes.OverlayColour;
import org.peakaboo.display.map.modes.OverlayPalette;
import org.peakaboo.display.map.modes.RatioPalette;

import com.google.common.base.Function;

import cyclops.Coord;
import cyclops.Pair;
import cyclops.Ratios;
import cyclops.Spectrum;
import cyclops.SpectrumCalculations;
import cyclops.visualization.Buffer;
import cyclops.visualization.Surface;
import cyclops.visualization.Surface.CompositeModes;
import cyclops.visualization.drawing.DrawingRequest;
import cyclops.visualization.drawing.ViewTransform;
import cyclops.visualization.drawing.map.MapDrawing;
import cyclops.visualization.drawing.map.painters.FloodMapPainter;
import cyclops.visualization.drawing.map.painters.MapPainter;
import cyclops.visualization.drawing.map.painters.MapTechniqueFactory;
import cyclops.visualization.drawing.map.painters.RasterColorMapPainter;
import cyclops.visualization.drawing.map.painters.RasterSpectrumMapPainter;
import cyclops.visualization.drawing.map.painters.SelectionMaskPainter;
import cyclops.visualization.drawing.map.painters.SpectrumMapPainter;
import cyclops.visualization.drawing.map.painters.axis.LegendCoordsAxisPainter;
import cyclops.visualization.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import cyclops.visualization.drawing.painters.PainterData;
import cyclops.visualization.drawing.painters.axis.AxisPainter;
import cyclops.visualization.drawing.painters.axis.PaddingAxisPainter;
import cyclops.visualization.drawing.painters.axis.TitleAxisPainter;
import cyclops.visualization.palette.PaletteColour;
import cyclops.visualization.palette.Spectrums;
import cyclops.visualization.palette.palettes.AbstractPalette;
import cyclops.visualization.palette.palettes.SaturationPalette;
import cyclops.visualization.palette.palettes.ThermalScalePalette;

public class Mapper {

	private DrawingRequest dr;
	private SpectrumMapPainter contourMapPainter, ratioMapPainter;
	private RasterSpectrumMapPainter overlayMapPainterRed, overlayMapPainterGreen, overlayMapPainterBlue, overlayMapPainterYellow;
	
	private SelectionMaskPainter selectionPainter;
	
	private MapDrawing map;
	
	
	public Mapper() {
		dr = new DrawingRequest();
		map = new MapDrawing(null, dr);
	}
	
	
	
	private Coord<Integer> setDimensions(MapRenderSettings settings, Coord<Integer> size) {
		
		if (settings == null) {
			settings = new MapRenderSettings();
		}

		
		double width = 0;
		double height = 0;
		
		if (size != null) {
			width = size.x;
			height = size.y;
		}
		
		//Auto-detect dimensions
		if (size == null || (size.x == 0 && size.y == 0)) {
			dr.imageWidth = 1000;
			dr.imageHeight = 1000;
			Coord<Float> newsize = map.calcTotalSize();
			width = newsize.x;
			height = newsize.y;
		}
		else if (size.x == 0) {
			dr.imageWidth = dr.imageHeight * 10;
			width = map.calcTotalSize().x;
			
		}
		else if (size.y == 0) {
			dr.imageHeight = dr.imageWidth * 10;
			height = map.calcTotalSize().y;
		}
		
		size = new Coord<Integer>((int)Math.round(width), (int)Math.round(height));
		
		dr.dataHeight = settings.userDataHeight;
		dr.dataWidth = settings.userDataWidth;
		dr.imageWidth = (float)size.x;
		dr.imageHeight = (float)size.y;
		
		return size;
		
	}
	
	
	public MapDrawing draw(MapRenderData data, MapRenderSettings settings, Surface context, Coord<Integer> size) {
	
		if (settings == null) {
			settings = new MapRenderSettings();
		}

		size = this.setDimensions(settings, size);
				
		map.setContext(context);

		
		final int spectrumSteps = (settings.contours) ? settings.contourSteps : Spectrums.DEFAULT_STEPS;
		
		//clear background with white
		context.rectAt(0, 0, (float)size.x, (float)size.y);
		context.setSource(new PaletteColour(0xffffffff));
		context.fill();
		
		switch (settings.mode)
		{
			case COMPOSITE:
				drawBackendComposite(data, settings, context, context.isVectorSurface(), spectrumSteps);
				break;
				
			case OVERLAY:
				drawBackendOverlay(data, settings, context, context.isVectorSurface(), spectrumSteps);
				break;
				
			case RATIO:
				drawBackendRatio(data, settings, context, context.isVectorSurface(), spectrumSteps);
				break;
				
		}
		
		return map;
		
	}

	public MapDrawing getMap() {
		return map;
	}
	
	public Coord<Integer> getCoordinate(float x, float y, boolean allowOutOfBounds) {

		if (map == null) return null;
		return map.getMapCoordinateAtPoint(x, y, allowOutOfBounds);

	}

	private AxisPainter getDescriptionPainter(MapRenderSettings settings) {
		String title = settings.spectrumTitle;
		if (!settings.calibrationProfile.isEmpty()) {
			title += " calibrated with " + settings.calibrationProfile.getName();	
		}
		return new TitleAxisPainter(TitleAxisPainter.SCALE_TEXT, null, null, null, title);
	}

	
	

	
	
	
	

	/**
	 * Drawing logic for the composite view
	 * @param backend surface to draw to
	 * @param vector is this a vector-based backend
	 * @param spectrumSteps how many steps should our legeng spectrum have
	 */
	private void drawBackendComposite(MapRenderData data, MapRenderSettings settings, Surface backend, boolean vector, int spectrumSteps)
	{
		
		AbstractPalette palette 			=		new ThermalScalePalette(spectrumSteps, settings.monochrome);
		AxisPainter spectrumCoordPainter 	= 		null;
		List<AbstractPalette> paletteList	=		new ArrayList<AbstractPalette>();
		List<AxisPainter> axisPainters 		= 		new ArrayList<AxisPainter>();
		
				
		dr.uninterpolatedWidth = settings.userDataWidth;
		dr.uninterpolatedHeight = settings.userDataHeight;
		dr.dataWidth = settings.interpolatedWidth == 0 ? settings.userDataWidth : settings.interpolatedWidth;
		dr.dataHeight = settings.interpolatedHeight == 0 ? settings.userDataHeight : settings.interpolatedHeight;
		dr.viewTransform = settings.logTransform ? ViewTransform.LOG : ViewTransform.LINEAR;
		dr.screenOrientation = settings.screenOrientation;
		
		if (settings.scalemode == MapScaleMode.RELATIVE)
		{
			dr.maxYIntensity = data.compositeData.max();
		}
		else
		{
			dr.maxYIntensity = data.maxIntensity;
		}

		
		palette = new ThermalScalePalette(spectrumSteps, settings.monochrome);

		

		if (settings.showDatasetTitle) {
			axisPainters.add(new TitleAxisPainter(TitleAxisPainter.SCALE_TITLE, null, null, settings.datasetTitle, null));
		}

		if (settings.showMapTitle) {
			axisPainters.add(new TitleAxisPainter(TitleAxisPainter.SCALE_TITLE, null, null, null, settings.mapTitle));
		}
		
		axisPainters.add(new PaddingAxisPainter(0, 0, 10, 0));
		axisPainters.add(getDescriptionPainter(settings));
		
		spectrumCoordPainter = new SpectrumCoordsAxisPainter (

			settings.drawCoord,
			settings.coordLoXHiY,
			settings.coordHiXHiY,
			settings.coordLoXLoY,
			settings.coordHiXLoY,
			settings.physicalUnits,

			settings.showSpectrum,
			settings.spectrumHeight,
			spectrumSteps,
			paletteList,

			settings.physicalCoord,
			settings.showScaleBar
		);
		axisPainters.add(spectrumCoordPainter);

		
		boolean oldVector = dr.drawToVectorSurface;
		dr.drawToVectorSurface = vector;

		map.setContext(backend);
		map.setAxisPainters(axisPainters);
		


		paletteList.add(palette);
		
		List<MapPainter> mapPainters = new ArrayList<MapPainter>();
		if (contourMapPainter == null) {
			contourMapPainter = MapTechniqueFactory.getTechnique(paletteList, data.compositeData, spectrumSteps); 
		} else {
			contourMapPainter.setData(data.compositeData);
			contourMapPainter.setPalettes(paletteList);
		}
		mapPainters.add(contourMapPainter);
		
		
		//Selection Painter
		if (selectionPainter == null) {
			selectionPainter = new SelectionMaskPainter(new PaletteColour(0xffffffff), settings.selectedPoints, settings.userDataWidth, settings.userDataHeight);
		} else {
			selectionPainter.configure(settings.userDataWidth, settings.userDataHeight, settings.selectedPoints);
		}
		mapPainters.add(selectionPainter);
			
		
		map.setPainters(mapPainters);
		map.draw();

		dr.drawToVectorSurface = oldVector;

	}
	
	



	/**
	 * Drawing logic for the ratio view
	 * @param backend surface to draw to
	 * @param vector is this a vector-based backend
	 * @param spectrumSteps how many steps should our legeng spectrum have
	 */
	private void drawBackendRatio(MapRenderData data, MapRenderSettings settings, Surface backend, boolean vector, int spectrumSteps)
	{
		AxisPainter spectrumCoordPainter 	= 		null;
		List<AbstractPalette> paletteList	=		new ArrayList<AbstractPalette>();
		List<AxisPainter> axisPainters 		= 		new ArrayList<AxisPainter>();
		
		Pair<Spectrum, Spectrum> ratiodata = data.ratioData;
		
		dr.uninterpolatedWidth = settings.userDataWidth;
		dr.uninterpolatedHeight = settings.userDataHeight;
		dr.dataWidth = settings.interpolatedWidth == 0 ? settings.userDataWidth : settings.interpolatedWidth;
		dr.dataHeight = settings.interpolatedHeight == 0 ? settings.userDataHeight : settings.interpolatedHeight;
		//LOG view not supported
		dr.viewTransform = ViewTransform.LINEAR;
		dr.screenOrientation = settings.screenOrientation;
		
		
		//this is a valid ratio if there is at least 1 visible TS
		boolean validRatio = ratiodata.first.sum() != 0;
		
		
		//how many steps/markings will we display on the spectrum
		float steps = (float) Math.ceil(SpectrumCalculations.abs(ratiodata.first).max());
		dr.maxYIntensity = steps;
		
		
		
		//if this is a valid ratio, make a real colour palette -- otherwise, just a black palette
		if (validRatio)
		{
			paletteList.add(new RatioPalette(spectrumSteps, settings.monochrome));
		}
		
		
		
		//generate a list of markers to be drawn along the spectrum to indicate the ratio at those points
		List<Pair<Float, String>> spectrumMarkers = new ArrayList<Pair<Float, String>>();

		int increment = 1;
		if (steps > 8) increment = (int) Math.ceil(steps / 8);

		if (validRatio)
		{
			for (int i = -(int) steps; i <= (int) steps; i += increment)
			{
				float percent = 0.5f + 0.5f * (i / steps);				
				spectrumMarkers.add(new Pair<Float, String>(percent, Ratios.fromFloat(i, true)));
			}
		}
		
		
		

		
		//if we're showing a dataset title, add a title axis painter to put a title on the top
		if (settings.showDatasetTitle)
		{
			axisPainters.add(new TitleAxisPainter(TitleAxisPainter.SCALE_TITLE, null, null, settings.datasetTitle, null));
		}

		//if we're map title, add a title axis painter to put a title on the bottom
		if (settings.showMapTitle)
		{
			String mapTitle = settings.mapTitle;
			axisPainters.add(new TitleAxisPainter(TitleAxisPainter.SCALE_TITLE, null, null, null, mapTitle));
		}
		
		axisPainters.add(new PaddingAxisPainter(0, 0, 10, 0));
		axisPainters.add(getDescriptionPainter(settings));

		//create a new coordinate/axis painter using the values in the model
		spectrumCoordPainter = new SpectrumCoordsAxisPainter
		(
			settings.drawCoord,
			settings.coordLoXHiY,
			settings.coordHiXHiY,
			settings.coordLoXLoY,
			settings.coordHiXLoY,
			settings.physicalUnits,

			settings.showSpectrum,
			settings.spectrumHeight,
			spectrumSteps,
			paletteList,

			settings.physicalCoord,
			settings.showScaleBar,
			1,
			settings.mode == MapDisplayMode.RATIO,
			spectrumMarkers
		);
		axisPainters.add(spectrumCoordPainter);

		
		boolean oldVector = dr.drawToVectorSurface;
		dr.drawToVectorSurface = vector;

		map.setContext(backend);
		map.setAxisPainters(axisPainters);
		map.setDrawingRequest(dr);


		
		List<MapPainter> mapPainters = new ArrayList<MapPainter>();
		if (ratioMapPainter == null) {
			ratioMapPainter = MapTechniqueFactory.getTechnique(paletteList, ratiodata.first, spectrumSteps); 
		} else {
			ratioMapPainter.setData(ratiodata.first);
			ratioMapPainter.setPalettes(paletteList);
		}
		mapPainters.add(ratioMapPainter);
		
		

				
		
		Spectrum invalidPoints = ratiodata.second;
		final float datamax = dr.maxYIntensity;
		
		
		invalidPoints.map_i((Float value) -> {
			if (value == 1f) return datamax;
			return 0f;
		});
		

		MapPainter invalidPainter = MapTechniqueFactory.getTechnique(new SaturationPalette(new PaletteColour(0xff777777), new PaletteColour(0x00000000)), invalidPoints, 0);
		mapPainters.add(invalidPainter);
		
		
		//Selection Painter
		if (selectionPainter == null) {
			selectionPainter = new SelectionMaskPainter(new PaletteColour(0xffffffff), settings.selectedPoints, settings.userDataWidth, settings.userDataHeight);
		} else {
			selectionPainter.configure(settings.userDataWidth, settings.userDataHeight, settings.selectedPoints);
		}
		mapPainters.add(selectionPainter);
		
		
		map.setPainters(mapPainters);
		map.draw();

		
		dr.drawToVectorSurface = oldVector;
		
	}
	
	/**
	 * Drawing logic for the overlay view
	 * @param backend surface to draw to
	 * @param vector is this a vector-based backend
	 * @param spectrumSteps how many steps should our legeng spectrum have
	 */
	private void drawBackendOverlay(MapRenderData data, MapRenderSettings settings, Surface backend, boolean vector, int spectrumSteps)
	{
		AxisPainter spectrumCoordPainter 	= 		null;
		List<AxisPainter> axisPainters 		= 		new ArrayList<AxisPainter>();
		

		
		
		dr.uninterpolatedWidth = settings.userDataWidth;
		dr.uninterpolatedHeight = settings.userDataHeight;
		dr.dataWidth = settings.interpolatedWidth == 0 ? settings.userDataWidth : settings.interpolatedWidth;
		dr.dataHeight = settings.interpolatedHeight == 0 ? settings.userDataHeight : settings.interpolatedHeight;
		dr.viewTransform = settings.logTransform ? ViewTransform.LOG : ViewTransform.LINEAR;
		dr.screenOrientation = settings.screenOrientation;
		
		Float redMax = 0f, greenMax = 0f, blueMax = 0f, yellowMax=0f;
		
		Spectrum redSpectrum = data.overlayData.get(OverlayColour.RED).data;
		Spectrum greenSpectrum = data.overlayData.get(OverlayColour.GREEN).data;
		Spectrum blueSpectrum = data.overlayData.get(OverlayColour.BLUE).data;
		Spectrum yellowSpectrum = data.overlayData.get(OverlayColour.YELLOW).data;
		
		
		if (redSpectrum != null ) redMax = redSpectrum.max();
		if (greenSpectrum != null ) greenMax = greenSpectrum.max();
		if (blueSpectrum != null ) blueMax = blueSpectrum.max();
		if (yellowSpectrum != null ) yellowMax = yellowSpectrum.max();
		
		
		dr.maxYIntensity = Math.max(Math.max(redMax, yellowMax), Math.max(greenMax, blueMax));


		List<Pair<PaletteColour, String>> 	colours = new ArrayList<>();
		Function<OverlayColour, String> tsFormatter = colour -> data.overlayData.get(colour).elements.stream()
				.map(ts -> ts.toString())
				.collect(Collectors.reducing((a, b) -> a + ", " + b)).orElse("");
		
		if (redSpectrum != null) 	colours.add(new Pair<>(OverlayColour.RED.toColour(), tsFormatter.apply(OverlayColour.RED)));
		if (yellowSpectrum != null) colours.add(new Pair<>(OverlayColour.YELLOW.toColour(), tsFormatter.apply(OverlayColour.YELLOW)));
		if (greenSpectrum != null) 	colours.add(new Pair<>(OverlayColour.GREEN.toColour(), tsFormatter.apply(OverlayColour.GREEN)));
		if (blueSpectrum != null) 	colours.add(new Pair<>(OverlayColour.BLUE.toColour(), tsFormatter.apply(OverlayColour.BLUE)));
		
		
		
		spectrumCoordPainter = new LegendCoordsAxisPainter(

			settings.drawCoord,
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

			

		if (settings.showDatasetTitle)
		{
			axisPainters.add(new TitleAxisPainter(TitleAxisPainter.SCALE_TITLE, null, null, settings.datasetTitle, null));
		}

		if (settings.showMapTitle)
		{
			String mapTitle = settings.mapTitle;
			axisPainters.add(new TitleAxisPainter(TitleAxisPainter.SCALE_TITLE, null, null, null, mapTitle));
		}

		axisPainters.add(new PaddingAxisPainter(0, 0, 10, 0));
		axisPainters.add(getDescriptionPainter(settings));
		
		axisPainters.add(spectrumCoordPainter);

		dr.drawToVectorSurface = vector;

		
		map.setAxisPainters(axisPainters);
		map.setDrawingRequest(dr);

	

		// create a list of map painters, one for each of the maps we want to show
		
		
		if (redSpectrum != null) {
			OverlayPalette palette = new OverlayPalette(spectrumSteps, OverlayColour.RED.toColour());
			palette.setLowCutoff(settings.overlayLowCutoff);
			if (overlayMapPainterRed == null) {
				overlayMapPainterRed = new RasterSpectrumMapPainter(palette, redSpectrum);
				overlayMapPainterRed.setCompositeMode(CompositeModes.ADD);
			}
			overlayMapPainterRed.setData(redSpectrum);
			overlayMapPainterRed.setPalette(palette);
			
		}
			
		if (greenSpectrum != null) {
			OverlayPalette palette = new OverlayPalette(spectrumSteps, OverlayColour.GREEN.toColour());
			palette.setLowCutoff(settings.overlayLowCutoff);
			if (overlayMapPainterGreen == null) {
				overlayMapPainterGreen = new RasterSpectrumMapPainter(palette, greenSpectrum);
				overlayMapPainterGreen.setCompositeMode(CompositeModes.ADD);
			}
			overlayMapPainterGreen.setData(greenSpectrum);
			overlayMapPainterGreen.setPalette(palette);
		}
		
		if (blueSpectrum != null) {
			OverlayPalette palette = new OverlayPalette(spectrumSteps, OverlayColour.BLUE.toColour());
			palette.setLowCutoff(settings.overlayLowCutoff);
			if (overlayMapPainterBlue == null) {
				overlayMapPainterBlue = new RasterSpectrumMapPainter(palette, blueSpectrum);
				overlayMapPainterBlue.setCompositeMode(CompositeModes.ADD);
			}
			overlayMapPainterBlue.setData(blueSpectrum);
			overlayMapPainterBlue.setPalette(palette);
		}
		
		if (yellowSpectrum != null) {
			OverlayPalette palette = new OverlayPalette(spectrumSteps, OverlayColour.YELLOW.toColour());
			palette.setLowCutoff(settings.overlayLowCutoff);
			if (overlayMapPainterYellow == null) {
				overlayMapPainterYellow = new RasterSpectrumMapPainter(palette, yellowSpectrum);
				overlayMapPainterYellow.setCompositeMode(CompositeModes.ADD);
			}
			overlayMapPainterYellow.setData(yellowSpectrum);
			overlayMapPainterYellow.setPalette(palette);
		}

		
		
		
		//Selection Painter
		if (selectionPainter == null) {
			selectionPainter = new SelectionMaskPainter(new PaletteColour(0xffffffff), settings.selectedPoints, settings.userDataWidth, settings.userDataHeight);
		} else {
			selectionPainter.configure(settings.userDataWidth, settings.userDataHeight, settings.selectedPoints);
		}
		

		
		
		if (vector) {
						
			//create new buffer to add the rgby channels in
			Buffer buffer = backend.getImageBuffer(settings.userDataWidth, settings.userDataHeight);
			PainterData p = new PainterData(buffer, dr, new Coord<Float>((float)dr.dataWidth, (float)dr.dataHeight), null);
			
			/*
			 * Hacky! Go through each colour painter and have it calculate the colour it
			 * would use to draw each pixel, adding them as it goes, until it can create a
			 * list of colours to feed to a RasterColourMapPainter.
			 */
			List<PaletteColour> addedColours = new ArrayList<>();
			for (int i = 0; i < dr.dataWidth * dr.dataHeight; i++) {
				PaletteColour addedColour = new PaletteColour();
				
				if (redSpectrum != null && overlayMapPainterRed != null) {
					addedColour = addedColour.add(overlayMapPainterRed.getColourFromRules(redSpectrum.get(i), overlayMapPainterRed.calcMaxIntensity(p), dr.viewTransform));
				}
				if (greenSpectrum != null && overlayMapPainterGreen != null) {
					addedColour = addedColour.add(overlayMapPainterGreen.getColourFromRules(greenSpectrum.get(i), overlayMapPainterGreen.calcMaxIntensity(p), dr.viewTransform));
				}
				if (blueSpectrum != null && overlayMapPainterBlue != null) {
					addedColour = addedColour.add(overlayMapPainterBlue.getColourFromRules(blueSpectrum.get(i), overlayMapPainterBlue.calcMaxIntensity(p), dr.viewTransform));
				}
				if (yellowSpectrum != null && overlayMapPainterYellow != null) {
					addedColour = addedColour.add(overlayMapPainterYellow.getColourFromRules(yellowSpectrum.get(i), overlayMapPainterYellow.calcMaxIntensity(p), dr.viewTransform));
				}
				addedColours.add(addedColour);
			}
			
			
			//get the pixels from the buffer as PaletteColour objects and pass them to a RasterColourMapPainter
			RasterColorMapPainter addedColoursMapPainter = new RasterColorMapPainter();
			addedColoursMapPainter.setPixels(addedColours);
			
			//set up the list of painters
			List<MapPainter> painters = new ArrayList<MapPainter>();
			painters.add(new FloodMapPainter(new PaletteColour(0xff000000))); //background
			painters.add(addedColoursMapPainter);
			painters.add(selectionPainter);
			map.setPainters(painters);
			
			//draw to the real backend
			map.setContext(backend);
			map.draw();
			
		} else {
			
			List<MapPainter> painters = new ArrayList<MapPainter>();
			painters.add(new FloodMapPainter(new PaletteColour(0xff000000))); //background
			if (redSpectrum != null && overlayMapPainterRed != null)       painters.add(overlayMapPainterRed);
			if (greenSpectrum != null && overlayMapPainterGreen != null)   painters.add(overlayMapPainterGreen);
			if (blueSpectrum != null && overlayMapPainterBlue != null)     painters.add(overlayMapPainterBlue);
			if (yellowSpectrum != null && overlayMapPainterYellow != null) painters.add(overlayMapPainterYellow);
			painters.add(selectionPainter);
			map.setPainters(painters);
			
			map.setContext(backend);
			map.draw();
		}
		
	}

	
	
	
	public void setNeedsRedraw()
	{
		map.needsMapRepaint();
		
		if (contourMapPainter != null)			contourMapPainter.clearBuffer();
		if (ratioMapPainter != null) 			ratioMapPainter.clearBuffer();
		if (overlayMapPainterBlue != null) 		overlayMapPainterBlue.clearBuffer();
		if (overlayMapPainterGreen != null)		overlayMapPainterGreen.clearBuffer();
		if (overlayMapPainterRed != null) 		overlayMapPainterRed.clearBuffer();
		if (overlayMapPainterYellow != null) 	overlayMapPainterYellow.clearBuffer();
	}

	
	
	
}
