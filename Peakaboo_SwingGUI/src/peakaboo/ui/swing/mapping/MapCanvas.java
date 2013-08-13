package peakaboo.ui.swing.mapping;

import static fava.Fn.filter;
import static fava.Fn.foldr;
import static fava.Fn.map;
import static fava.Fn.unique;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.maptab.MapDisplayMode;
import peakaboo.controller.mapper.maptab.MapScaleMode;
import peakaboo.controller.mapper.maptab.MapTabController;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.mapping.colours.OverlayColour;
import scidraw.drawing.DrawingRequest;
import scidraw.drawing.backends.Surface;
import scidraw.drawing.backends.Surface.CompositeModes;
import scidraw.drawing.common.Spectrums;
import scidraw.drawing.map.MapDrawing;
import scidraw.drawing.map.painters.BoundedRegionPainter;
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
import scidraw.swing.GraphicsPanel;
import scitypes.Bounds;
import scitypes.Coord;
import scitypes.Ratios;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;
import fava.datatypes.Pair;
import fava.signatures.FnCondition;
import fava.signatures.FnFold;
import fava.signatures.FnMap;


public class MapCanvas extends GraphicsPanel
{

	MappingController 		controller;
	MapTabController		tabController;
	DrawingRequest 		dr;
	
	private SpectrumMapPainter contourMapPainter, ratioMapPainter, overlayMapPainterRed, overlayMapPainterGreen, overlayMapPainterBlue;
	private MapDrawing	map;
	
	private static final int	SPECTRUM_HEIGHT = 15;
	
	public MapCanvas(MappingController controller, MapTabController tabController)
	{
		this.controller = controller;
		this.tabController = tabController;
		
		dr = new DrawingRequest();
		map = new MapDrawing(null, dr);
	}
	
	@Override
	protected void drawGraphics(Surface backend, boolean vector)
	{
		drawMap(backend, vector);
	}

	@Override
	public float getUsedHeight()
	{
		return map.calcTotalSize().y;
	}

	@Override
	public float getUsedWidth()
	{
		return map.calcTotalSize().x;
	}

	
	
	
	
	
	

	public Coord<Integer> getMapCoordinateAtPoint(float x, float y, boolean allowOutOfBounds)
	{

		if (map == null) return null;
		return map.getMapCoordinateAtPoint(x, y, allowOutOfBounds);

	}


	public Coord<Integer> getPointForMapCoordinate(Coord<Integer> coord)
	{
		if (map == null) return null;

		Coord<Bounds<Float>> borders = map.calcAxisBorders();
		float topOffset, leftOffset;
		topOffset = borders.y.start;
		leftOffset = borders.x.start;

		Coord<Float> mapSize = map.calcMapSize();
		int locX, locY;
		locX = (int) (leftOffset + (((float) coord.x / (float) controller.mapsController.getDataWidth()) * mapSize.x) - (MapDrawing
			.calcInterpolatedCellSize(mapSize.x, mapSize.y, dr) * 0.5));
		locY = (int) (topOffset + (((float) coord.y / (float) controller.mapsController.getDataHeight()) * mapSize.y) - (MapDrawing
			.calcInterpolatedCellSize(mapSize.x, mapSize.y, dr) * 0.5));

		return new Coord<Integer>(locX, locY);
	}

	
	
	
	
	
	

	/**
	 * Drawing logic for the composite view
	 * @param backend surface to draw to
	 * @param vector is this a vector-based backend
	 * @param spectrumSteps how many steps should our legeng spectrum have
	 */
	private void drawBackendComposite(Surface backend, boolean vector, int spectrumSteps)
	{
		
		AbstractPalette palette 			=		new ThermalScalePalette(spectrumSteps, controller.mapsController.getMonochrome());
		AxisPainter spectrumCoordPainter 	= 		null;
		List<AbstractPalette> paletteList	=		new ArrayList<AbstractPalette>();
		List<AxisPainter> axisPainters 		= 		new ArrayList<AxisPainter>();
		
		
		Spectrum data = tabController.getCompositeMapData();
		
		dr.uninterpolatedWidth = controller.mapsController.getDataWidth();
		dr.uninterpolatedHeight = controller.mapsController.getDataHeight();
		dr.dataWidth = controller.mapsController.getInterpolatedWidth();
		dr.dataHeight = controller.mapsController.getInterpolatedHeight();
		
		if (tabController.getMapScaleMode() == MapScaleMode.RELATIVE)
		{
			dr.maxYIntensity = SpectrumCalculations.max(data);
		}
		else
		{
			dr.maxYIntensity = SpectrumCalculations.max(tabController.sumAllTransitionSeriesMaps());
		}

		
		palette = new ThermalScalePalette(spectrumSteps, controller.mapsController.getMonochrome());

		

		if (controller.mapsController.getShowDatasetTitle())
		{
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, controller.mapsController.getDatasetTitle(), null));
		}

		if (controller.mapsController.getShowTitle())
		{
			String mapTitle = "";

			if (tabController.getVisibleTransitionSeries().size() > 1)
			{
				mapTitle = "Composite of " + tabController.mapLongTitle();
			}
			else
			{
				mapTitle = "Map of " + tabController.mapLongTitle();
			}
			
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, null, mapTitle));
		}
		
		
		spectrumCoordPainter = new SpectrumCoordsAxisPainter
		(

			controller.mapsController.getDrawCoords(),
			controller.mapsController.getBottomLeftCoord(),
			controller.mapsController.getBottomRightCoord(),
			controller.mapsController.getTopLeftCoord(),
			controller.mapsController.getTopRightCoord(),
			controller.mapsController.getRealDimensionUnits(),

			controller.mapsController.getShowSpectrum(),
			SPECTRUM_HEIGHT,
			spectrumSteps,
			paletteList,

			controller.mapsController.isDimensionsProvided(),
			"Intensity (counts)"
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
			contourMapPainter = MapTechniqueFactory.getTechnique(paletteList, data, controller.mapsController.getContours(), spectrumSteps); 
		} else {
			/*Spectrum modData = SpectrumCalculations.gridYReverse(
					data, 
					new GridPerspective<Float>(dr.dataWidth, dr.dataHeight, 0f));*/
			contourMapPainter.setData(data);
			contourMapPainter.setPalettes(paletteList);
		}
		mapPainters.add(contourMapPainter);
		
		
		if (tabController.hasBoundingRegion())
		{
			mapPainters.add(new BoundedRegionPainter(Color.white, tabController.getDragStart(), tabController.getDragEnd()));
		}
		
		
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
	private void drawBackendRatio(Surface backend, boolean vector, int spectrumSteps)
	{
		AxisPainter spectrumCoordPainter 	= 		null;
		List<AbstractPalette> paletteList	=		new ArrayList<AbstractPalette>();
		List<AxisPainter> axisPainters 		= 		new ArrayList<AxisPainter>();
		
		Pair<Spectrum, Spectrum> ratiodata = tabController.getRatioMapData();
		
		dr.uninterpolatedWidth = controller.mapsController.getDataWidth();
		dr.uninterpolatedHeight = controller.mapsController.getDataHeight();
		dr.dataWidth = controller.mapsController.getInterpolatedWidth();
		dr.dataHeight = controller.mapsController.getInterpolatedHeight();
		
		
		//create a unique list of the represented sides of the ratio from the set of visible TransitionSeries
		List<Integer> ratioSideValues = unique(map(tabController.getVisibleTransitionSeries(), new FnMap<TransitionSeries, Integer>() {

			public Integer f(TransitionSeries ts)
			{
				return tabController.getRatioSide(ts);
			}
		}));
		
		
		//this is a valid ratio if there is at least 1 visible TS for each side
		boolean validRatio = (ratioSideValues.contains(1) && ratioSideValues.contains(2));

		
		//how many steps/markings will we display on the spectrum
		float steps = (float) Math.ceil(SpectrumCalculations.max(SpectrumCalculations.abs(ratiodata.first)));
		dr.maxYIntensity = steps;
		
		
		
		//if this is a valid ratio, make a real colour palette -- otherwise, just a black palette
		if (validRatio)
		{
			paletteList.add(new RatioPalette(spectrumSteps, controller.mapsController.getMonochrome()));
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
		if (controller.mapsController.getShowDatasetTitle())
		{
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, controller.mapsController.getDatasetTitle(), null));
		}

		//if we're map title, add a title axis painter to put a title on the bottom
		if (controller.mapsController.getShowTitle())
		{
			String mapTitle = "";
			mapTitle = tabController.mapLongTitle();
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, null, mapTitle));
		}
		

		//create a new coordinate/axis painter using the values in the model
		spectrumCoordPainter = new SpectrumCoordsAxisPainter
		(
			controller.mapsController.getDrawCoords(),
			controller.mapsController.getBottomLeftCoord(),
			controller.mapsController.getBottomRightCoord(),
			controller.mapsController.getTopLeftCoord(),
			controller.mapsController.getTopRightCoord(),
			controller.mapsController.getRealDimensionUnits(),

			controller.mapsController.getShowSpectrum(),
			SPECTRUM_HEIGHT,
			spectrumSteps,
			paletteList,

			controller.mapsController.isDimensionsProvided(),
			"Intensity (ratio)" + (tabController.getMapScaleMode() == MapScaleMode.RELATIVE ? " - Ratio sides scaled independently" : ""),
			1,
			tabController.getMapDisplayMode() == MapDisplayMode.RATIO,
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
			ratioMapPainter = MapTechniqueFactory.getTechnique(paletteList, ratiodata.first, controller.mapsController.getContours(), spectrumSteps); 
		} else {
			ratioMapPainter.setData(ratiodata.first);
			ratioMapPainter.setPalettes(paletteList);
		}
		mapPainters.add(ratioMapPainter);
		
		

				
		
		Spectrum invalidPoints = ratiodata.second;
		final float datamax = dr.maxYIntensity;
		
		
		invalidPoints.map_i(new FnMap<Float, Float>() {

			public Float f(Float value)
			{
				if (value == 1f) return datamax;
				return 0f;
			}});
		

		MapPainter invalidPainter = MapTechniqueFactory.getTechnique(new SaturationPalette(Color.gray, new Color(0,0,0,0)), invalidPoints, false, 0);
		mapPainters.add(invalidPainter);
		
		if (tabController.hasBoundingRegion())
		{
			mapPainters.add(new BoundedRegionPainter(Color.white, tabController.getDragStart(), tabController.getDragEnd()));
		}
		
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
	private void drawBackendOverlay(Surface backend, boolean vector, int spectrumSteps)
	{
		AxisPainter spectrumCoordPainter 	= 		null;
		List<AxisPainter> axisPainters 		= 		new ArrayList<AxisPainter>();
		
		Map<OverlayColour, Spectrum> data = tabController.getOverlayMapData();
		
		
		dr.uninterpolatedWidth = controller.mapsController.getDataWidth();
		dr.uninterpolatedHeight = controller.mapsController.getDataHeight();
		dr.dataWidth = controller.mapsController.getInterpolatedWidth();
		dr.dataHeight = controller.mapsController.getInterpolatedHeight();
		
		
		
		Float redMax = 0f, greenMax = 0f, blueMax = 0f;
		
		Spectrum redSpectrum = data.get(OverlayColour.RED);
		Spectrum greenSpectrum = data.get(OverlayColour.GREEN);
		Spectrum blueSpectrum = data.get(OverlayColour.BLUE);
		
		
		if (redSpectrum != null ) redMax = SpectrumCalculations.max(redSpectrum);
		if (greenSpectrum != null ) greenMax = SpectrumCalculations.max(greenSpectrum);
		if (blueSpectrum != null ) blueMax = SpectrumCalculations.max(blueSpectrum);
		
		
		dr.maxYIntensity = Math.max(redMax, Math.max(greenMax, blueMax));


		spectrumCoordPainter = new LegendCoordsAxisPainter(

			controller.mapsController.getDrawCoords(),
			controller.mapsController.getBottomLeftCoord(),
			controller.mapsController.getBottomRightCoord(),
			controller.mapsController.getTopLeftCoord(),
			controller.mapsController.getTopRightCoord(),
			controller.mapsController.getRealDimensionUnits(),

			controller.mapsController.getShowSpectrum(),
			SPECTRUM_HEIGHT,

			controller.mapsController.isDimensionsProvided(),
			"Colour" + (tabController.getMapScaleMode() == MapScaleMode.RELATIVE ? " - Colours scaled independently" : ""),

			// create a list of color,string pairs for the legend by mapping the list of transitionseries per
			// colour and filter for empty strings
			filter(
					map(

					// input list - get a unique list of colours in use

							unique(tabController.getOverlayColourValues()),

							// mapping function - convert the color objects into color,string pairs (ie
							// color/element
							// list)
							new FnMap<OverlayColour, Pair<Color, String>>() {

								
								public Pair<Color, String> f(final OverlayColour ocolour)
								{
									// create a color,string pair
									return new Pair<Color, String>(

									ocolour.toColor(),

									// fold the list of transition series using the concat operator
										foldr(

												//grab a list of all TSs from the TS->Colour map and filter for the right colour
												filter(
														
													tabController.getOverlayColourKeys(), 
													
													new FnCondition<TransitionSeries>() {
														public Boolean f(TransitionSeries ts)
														{
															return tabController.getOverlayColour(ts) == ocolour;
														}
													}
													
												)//filter transitionseries
												,
												"",
												new FnFold<TransitionSeries, String>() {

													
													public String f(TransitionSeries ts, String title)
													{
														return title + (title.equals("") ? "" : ", ")
																+ ts.toElementString();
													}
												}
										) //foldr [TransitionSeries] -> String (title)

									);
								}
							}),

					// filter for empty strings
					new FnCondition<Pair<Color, String>>() {

						
						public Boolean f(Pair<Color, String> element)
							{
								return !(element.second.length() == 0);
							}
					})

		);

			

		if (controller.mapsController.getShowDatasetTitle())
		{
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, controller.mapsController.getDatasetTitle(), null));
		}

		if (controller.mapsController.getShowTitle())
		{
			String mapTitle = "";
			mapTitle = "Overlay of " + tabController.mapLongTitle();
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
		
		//need to paint the background black first
		painters.add(
				0, 
				new FloodMapPainter(Color.black)
		);
		
		if (tabController.hasBoundingRegion())
		{
			painters.add(new BoundedRegionPainter(Color.white, tabController.getDragStart(), tabController.getDragEnd()));
		}

		// set the new data
		map.setPainters(painters);
		map.draw();


		dr.drawToVectorSurface = oldVector;
		
	}
	
	
	
	protected void drawMap(Surface context, boolean vector)
	{
		
		context.rectangle(0, 0, getWidth(), getHeight());
		context.setSource(Color.white);
		context.fill();
		

		// Map Dimensions
		int originalWidth = controller.mapsController.getDataWidth();
		int originalHeight = controller.mapsController.getDataHeight();

		dr.dataHeight = controller.mapsController.getDataHeight();
		dr.dataWidth = controller.mapsController.getDataWidth();
		dr.imageWidth = getWidth();
		dr.imageHeight = getHeight();
		
		map.setContext(context);
		
		
		if (controller.mapsController.isDimensionsProvided())
		{

			Coord<Bounds<Number>> realDims = controller.mapsController.getRealDimensions();
			
			controller.mapsController.setMapCoords(
					new Coord<Number>( realDims.x.start, 	realDims.y.end),
					new Coord<Number>( realDims.x.end, 		realDims.y.end), 
					new Coord<Number>( realDims.x.start,	realDims.y.start), 
					new Coord<Number>( realDims.x.end,		realDims.y.start) 
					
					
				);

		}
		else
		{

			controller.mapsController.setMapCoords(
					new Coord<Number>(1, originalHeight),
					new Coord<Number>(originalWidth, originalHeight),
					new Coord<Number>(1, 1), 
					new Coord<Number>(originalWidth, 1)					
				);
		}
		
		
		final int spectrumSteps = (controller.mapsController.getContours()) ? controller.mapsController.getSpectrumSteps() : Spectrums.DEFAULT_STEPS;
		
		switch (tabController.getMapDisplayMode())
		{
			case COMPOSITE:
				drawBackendComposite(context, vector, spectrumSteps);
				break;
				
			case OVERLAY:
				drawBackendOverlay(context, vector, spectrumSteps);
				break;
				
			case RATIO:
				drawBackendRatio(context, vector, spectrumSteps);
				break;
				
		}
		
		return;
		
	}
	
	
	public void setNeedsRedraw()
	{
		map.needsMapRepaint();
		
		if (contourMapPainter != null)		contourMapPainter.clearBuffer();
		if (ratioMapPainter != null) 		ratioMapPainter.clearBuffer();
		if (overlayMapPainterBlue != null) 	overlayMapPainterBlue.clearBuffer();
		if (overlayMapPainterGreen != null)	overlayMapPainterGreen.clearBuffer();
		if (overlayMapPainterRed != null) 	overlayMapPainterRed.clearBuffer();
	}
	
}
