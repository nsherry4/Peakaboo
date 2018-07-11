package peakaboo.display.map;


import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import scidraw.drawing.DrawingRequest;
import scidraw.drawing.ViewTransform;
import scidraw.drawing.backends.Surface;
import scidraw.drawing.backends.Surface.CompositeModes;
import scidraw.drawing.common.Spectrums;
import scidraw.drawing.map.MapDrawing;
import scidraw.drawing.map.painters.FloodMapPainter;
import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.MapTechniqueFactory;
import scidraw.drawing.map.painters.RasterSpectrumMapPainter;
import scidraw.drawing.map.painters.SpectrumMapPainter;
import scidraw.drawing.map.painters.axis.LegendCoordsAxisPainter;
import scidraw.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.map.palettes.OverlayPalette;
import scidraw.drawing.map.palettes.RatioPalette;
import scidraw.drawing.map.palettes.SaturationPalette;
import scidraw.drawing.map.palettes.ThermalScalePalette;
import scidraw.drawing.painters.axis.AxisPainter;
import scidraw.drawing.painters.axis.TitleAxisPainter;
import scitypes.Pair;
import scitypes.Ratios;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

public class Mapper {

	private DrawingRequest dr;
	private SpectrumMapPainter contourMapPainter, ratioMapPainter, overlayMapPainterRed, overlayMapPainterGreen, overlayMapPainterBlue, overlayMapPainterYellow;
	
	private MapDrawing map;
	
	
	public Mapper() {
		dr = new DrawingRequest();
		map = new MapDrawing(null, dr);
	}
	
	public MapDrawing draw(MapData data, MapSettings settings, Surface context, boolean vector, Dimension size) {
	

		
		context.rectangle(0, 0, (float)size.getWidth(), (float)size.getHeight());
		context.setSource(Color.white);
		context.fill();

		dr.dataHeight = settings.dataHeight;
		dr.dataWidth = settings.dataWidth;
		dr.imageWidth = (float)size.getWidth();
		dr.imageHeight = (float)size.getHeight();
		
		map.setContext(context);

		
		final int spectrumSteps = (settings.contours) ? settings.contourSteps : Spectrums.DEFAULT_STEPS;
		
		switch (settings.mode)
		{
			case COMPOSITE:
				drawBackendComposite(data, settings, context, vector, spectrumSteps);
				break;
				
			case OVERLAY:
				drawBackendOverlay(data, settings, context, vector, spectrumSteps);
				break;
				
			case RATIO:
				drawBackendRatio(data, settings, context, vector, spectrumSteps);
				break;
				
		}
		
		return map;
		
	}

	public MapDrawing getMap() {
		return map;
	}
	
	
	

	
	
	
	

	/**
	 * Drawing logic for the composite view
	 * @param backend surface to draw to
	 * @param vector is this a vector-based backend
	 * @param spectrumSteps how many steps should our legeng spectrum have
	 */
	private void drawBackendComposite(MapData data, MapSettings settings, Surface backend, boolean vector, int spectrumSteps)
	{
		
		AbstractPalette palette 			=		new ThermalScalePalette(spectrumSteps, settings.monochrome);
		AxisPainter spectrumCoordPainter 	= 		null;
		List<AbstractPalette> paletteList	=		new ArrayList<AbstractPalette>();
		List<AxisPainter> axisPainters 		= 		new ArrayList<AxisPainter>();
		
				
		dr.uninterpolatedWidth = settings.dataWidth;
		dr.uninterpolatedHeight = settings.dataHeight;
		dr.dataWidth = settings.interpolatedWidth;
		dr.dataHeight = settings.interpolatedHeight;
		dr.viewTransform = settings.transform;

		
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
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, settings.datasetTitle, null));
		}

		if (settings.showMapTitle) {
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, null, settings.mapTitle));
		}
		
		
		spectrumCoordPainter = new SpectrumCoordsAxisPainter (

			settings.drawCoord,
			settings.coordBL,
			settings.coordBR,
			settings.coordTL,
			settings.coordTR,
			settings.physicalUnits,

			settings.showSpectrum,
			settings.spectrumHeight,
			spectrumSteps,
			paletteList,

			settings.physicalCoord,
			settings.spectrumTitle
		);
		axisPainters.add(spectrumCoordPainter);

		
		boolean oldVector = dr.drawToVectorSurface;
		dr.drawToVectorSurface = vector;

		map.setContext(backend);
		map.setAxisPainters(axisPainters);
		map.setDrawingRequest(dr);


		paletteList.add(palette);
		
		List<MapPainter> mapPainters = new ArrayList<MapPainter>();
		if (contourMapPainter == null) {
			contourMapPainter = MapTechniqueFactory.getTechnique(paletteList, data.compositeData, settings.contours, spectrumSteps); 
		} else {
			contourMapPainter.setData(data.compositeData);
			contourMapPainter.setPalettes(paletteList);
		}
		mapPainters.add(contourMapPainter);
		
		
		mapPainters.addAll(settings.painters);
			
		
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
	private void drawBackendRatio(MapData data, MapSettings settings, Surface backend, boolean vector, int spectrumSteps)
	{
		AxisPainter spectrumCoordPainter 	= 		null;
		List<AbstractPalette> paletteList	=		new ArrayList<AbstractPalette>();
		List<AxisPainter> axisPainters 		= 		new ArrayList<AxisPainter>();
		
		Pair<Spectrum, Spectrum> ratiodata = data.ratioData;
		
		dr.uninterpolatedWidth = settings.dataWidth;
		dr.uninterpolatedHeight = settings.dataHeight;
		dr.dataWidth = settings.interpolatedWidth;
		dr.dataHeight = settings.interpolatedHeight;
		//LOG view not supported
		dr.viewTransform = ViewTransform.LINEAR;
		
		
		//this is a valid ratio if there is at least 1 visible TS for each side
		boolean validRatio = (ratiodata.first.sum() > 0) && (ratiodata.second.sum() > 0);
		
		
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
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, settings.datasetTitle, null));
		}

		//if we're map title, add a title axis painter to put a title on the bottom
		if (settings.showMapTitle)
		{
			String mapTitle = settings.mapTitle;
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, null, mapTitle));
		}
		

		//create a new coordinate/axis painter using the values in the model
		spectrumCoordPainter = new SpectrumCoordsAxisPainter
		(
			settings.drawCoord,
			settings.coordBL,
			settings.coordBR,
			settings.coordTL,
			settings.coordTR,
			settings.physicalUnits,

			settings.showSpectrum,
			settings.spectrumHeight,
			spectrumSteps,
			paletteList,

			settings.physicalCoord,
			settings.spectrumTitle,
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
			ratioMapPainter = MapTechniqueFactory.getTechnique(paletteList, ratiodata.first, settings.contours, spectrumSteps); 
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
		

		MapPainter invalidPainter = MapTechniqueFactory.getTechnique(new SaturationPalette(Color.gray, new Color(0,0,0,0)), invalidPoints, false, 0);
		mapPainters.add(invalidPainter);
		
		
		mapPainters.addAll(settings.painters);
		
		
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
	private void drawBackendOverlay(MapData dlata, MapSettings settings, Surface backend, boolean vector, int spectrumSteps)
	{
		AxisPainter spectrumCoordPainter 	= 		null;
		List<AxisPainter> axisPainters 		= 		new ArrayList<AxisPainter>();
		

		
		
		dr.uninterpolatedWidth = settings.dataWidth;
		dr.uninterpolatedHeight = settings.dataHeight;
		dr.dataWidth = settings.interpolatedWidth;
		dr.dataHeight = settings.interpolatedHeight;
		dr.viewTransform = settings.transform;
		
		
		Float redMax = 0f, greenMax = 0f, blueMax = 0f, yellowMax=0f;
		
		Spectrum redSpectrum = dlata.overlayData.get(OverlayColour.RED);
		Spectrum greenSpectrum = dlata.overlayData.get(OverlayColour.GREEN);
		Spectrum blueSpectrum = dlata.overlayData.get(OverlayColour.BLUE);
		Spectrum yellowSpectrum = dlata.overlayData.get(OverlayColour.YELLOW);
		
		
		if (redSpectrum != null ) redMax = redSpectrum.max();
		if (greenSpectrum != null ) greenMax = greenSpectrum.max();
		if (blueSpectrum != null ) blueMax = blueSpectrum.max();
		if (yellowSpectrum != null ) yellowMax = yellowSpectrum.max();
		
		
		dr.maxYIntensity = Math.max(Math.max(redMax, yellowMax), Math.max(greenMax, blueMax));


		List<Pair<Color, String>> 	colours = new ArrayList<>();
		if (redSpectrum != null) 	colours.add(new Pair<>(OverlayColour.RED.toColor(), OverlayColour.RED.toString()));
		if (yellowSpectrum != null) colours.add(new Pair<>(OverlayColour.YELLOW.toColor(), OverlayColour.YELLOW.toString()));
		if (greenSpectrum != null) 	colours.add(new Pair<>(OverlayColour.GREEN.toColor(), OverlayColour.GREEN.toString()));
		if (blueSpectrum != null) 	colours.add(new Pair<>(OverlayColour.BLUE.toColor(), OverlayColour.BLUE.toString()));
		
		spectrumCoordPainter = new LegendCoordsAxisPainter(

			settings.drawCoord,
			settings.coordBL,
			settings.coordBR,
			settings.coordTL,
			settings.coordTR,
			settings.physicalUnits,

			settings.showSpectrum,
			settings.spectrumHeight,

			settings.physicalCoord,
			settings.spectrumTitle,

			colours
			
		);

			

		if (settings.showDatasetTitle)
		{
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, settings.datasetTitle, null));
		}

		if (settings.showMapTitle)
		{
			String mapTitle = settings.mapTitle;
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, null, mapTitle));
		}

		axisPainters.add(spectrumCoordPainter);

		boolean oldVector = dr.drawToVectorSurface;
		dr.drawToVectorSurface = vector;

		map.setContext(backend);
		map.setAxisPainters(axisPainters);
		map.setDrawingRequest(dr);

	

		// create a list of map painters, one for each of the maps we want to show
		List<MapPainter> painters = new ArrayList<MapPainter>();
		
		if (redSpectrum != null){
			if (overlayMapPainterRed == null) {
				overlayMapPainterRed = new RasterSpectrumMapPainter(new OverlayPalette(spectrumSteps, OverlayColour.RED.toColor()), redSpectrum);
				overlayMapPainterRed.setCompositeMode(CompositeModes.ADD);
			}
			overlayMapPainterRed.setData(redSpectrum);
			overlayMapPainterRed.setPalette(new OverlayPalette(spectrumSteps, OverlayColour.RED.toColor()));
			painters.add(overlayMapPainterRed);
		}
			
		if (greenSpectrum != null) {
			if (overlayMapPainterGreen == null) {
				overlayMapPainterGreen = new RasterSpectrumMapPainter(new OverlayPalette(spectrumSteps, OverlayColour.GREEN.toColor()), greenSpectrum);
				overlayMapPainterGreen.setCompositeMode(CompositeModes.ADD);
			}
			overlayMapPainterGreen.setData(greenSpectrum);
			overlayMapPainterGreen.setPalette(new OverlayPalette(spectrumSteps, OverlayColour.GREEN.toColor()));
			painters.add(overlayMapPainterGreen);
		}
		
		if (blueSpectrum != null) {
			if (overlayMapPainterBlue == null) {
				overlayMapPainterBlue = new RasterSpectrumMapPainter(new OverlayPalette(spectrumSteps, OverlayColour.BLUE.toColor()), blueSpectrum);
				overlayMapPainterBlue.setCompositeMode(CompositeModes.ADD);
			}
			overlayMapPainterBlue.setData(blueSpectrum);
			overlayMapPainterBlue.setPalette(new OverlayPalette(spectrumSteps, OverlayColour.BLUE.toColor()));
			painters.add(overlayMapPainterBlue);
		}
		
		if (yellowSpectrum != null) {
			if (overlayMapPainterYellow == null) {
				overlayMapPainterYellow = new RasterSpectrumMapPainter(new OverlayPalette(spectrumSteps, OverlayColour.YELLOW.toColor()), yellowSpectrum);
				overlayMapPainterYellow.setCompositeMode(CompositeModes.ADD);
			}
			overlayMapPainterYellow.setData(yellowSpectrum);
			overlayMapPainterYellow.setPalette(new OverlayPalette(spectrumSteps, OverlayColour.YELLOW.toColor()));
			painters.add(overlayMapPainterYellow);
		}
		
		//need to paint the background black first
		painters.add(
				0, 
				new FloodMapPainter(Color.black)
		);
		
		
		
		painters.addAll(settings.painters);

		
		
		// set the new data
		map.setPainters(painters);
		map.draw();


		dr.drawToVectorSurface = oldVector;
		
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
