package peakaboo.controller.mapper;



import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import fava.*;
import fava.datatypes.Bounds;
import fava.datatypes.Pair;
import fava.lists.FList;
import fava.signatures.FunctionCombine;
import fava.signatures.FunctionEach;
import fava.signatures.FunctionMap;
import static fava.Fn.*;
import static fava.Functions.*;

import peakaboo.calculations.Interpolation;
import peakaboo.controller.CanvasController;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.mapping.colours.OverlayColour;
import scidraw.drawing.backends.Surface;
import scidraw.drawing.backends.Surface.CompositeModes;
import scidraw.drawing.common.Spectrums;
import scidraw.drawing.map.MapDrawing;
import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.MapTechniqueFactory;
import scidraw.drawing.map.painters.axis.LegendCoordsAxisPainter;
import scidraw.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.map.palettes.OverlayPalette;
import scidraw.drawing.map.palettes.RatioPalette;
import scidraw.drawing.map.palettes.SingleColourPalette;
import scidraw.drawing.map.palettes.ThermalScalePalette;
import scidraw.drawing.painters.PainterData;
import scidraw.drawing.painters.axis.AxisPainter;
import scidraw.drawing.painters.axis.TitleAxisPainter;
import scitypes.Coord;
import scitypes.GridPerspective;
import scitypes.Ratios;
import scitypes.SigDigits;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;



public class MapController extends CanvasController
{

	private Coord<Integer>		linearSampleStart, linearSampleStop;
	private static final long	serialVersionUID	= 1L;
	private SingleMapModel		activeTabData;
	private AllMapsModel		mapModel;
	private MapDrawing			map;
	private String				datasetTitle;


	public MapController(Object toyContext, AllMapsModel model)
	{
		super(toyContext);
		mapModel = model;
		map = new MapDrawing(this.toyContext, mapModel.dr);
	}


	public SingleMapModel getActiveTabModel()
	{
		return activeTabData;
	}


	public void setActiveTabModel(SingleMapModel activeviewmodel)
	{
		this.activeTabData = activeviewmodel;
		updateListeners("");
	}


	private List<Spectrum> getVisibleMapData()
	{
		if (activeTabData.resultantData == null)
		{
			generateFinalData();
		}

		return map(activeTabData.resultantData, Functions.<TransitionSeries, Spectrum> second());
	}


	public String getIntensityMeasurementAtPoint(final Coord<Integer> mapCoord)
	{
		final GridPerspective<Float> mapGrid = new GridPerspective<Float>(getDataWidth(), getDataHeight(), null);
		Float value;

		switch (activeTabData.displayMode)
		{
			case COMPOSITE:

				value = mapGrid.get(getVisibleMapData().get(0), mapCoord.x, mapCoord.y);
				return SigDigits.roundFloatTo(value, 2);

			case OVERLAY:

				return foldr(

				map(getVisibleMapData(), new FunctionMap<Spectrum, String>() {


					public String f(Spectrum element)
					{
						return SigDigits.roundFloatTo(mapGrid.get(element, mapCoord.x, mapCoord.y), 2);
					}
				})

				, "", strcat());

			case RATIO:

				int side1Count = activeTabData.getTransitionSeriesForRatioSide(1).size();
				int side2Count = activeTabData.getTransitionSeriesForRatioSide(2).size();

				if (side1Count == 0 || side2Count == 0) return "-";

				value = mapGrid.get(getVisibleMapData().get(0), mapCoord.x, mapCoord.y);
				return Ratios.fromFloat(value);

		}
		return "";
	}


	public void setMapData(AllMapsModel data, String datasetName, Coord<Integer> dataDimensions)
	{

		this.mapModel = data;
		datasetTitle = datasetName;

		if (dataDimensions != null) mapModel.dataDimensions = dataDimensions;

		updateListeners("");

	}


	public boolean isValidPoint(Coord<Integer> mapCoord)
	{
		return (mapCoord.x >= 0 && mapCoord.x < getDataWidth() && mapCoord.y >= 0 && mapCoord.y < getDataHeight());
	}


	public int getMapSize()
	{
		return mapModel.mapSize();
	}


	public Coord<Integer> getLinearSampleStart()
	{
		return linearSampleStart;
	}


	public void setLinearSampleStart(Coord<Integer> linearSampleStart)
	{
		this.linearSampleStart = linearSampleStart;
	}


	public Coord<Integer> getLinearSampleStop()
	{
		return linearSampleStop;
	}


	public void setLinearSampleStop(Coord<Integer> linearSampleStop)
	{
		this.linearSampleStop = linearSampleStop;
	}


	public Coord<Integer> getMapCoordinateAtPoint(float x, float y)
	{

		if (map == null) return null;

		Coord<Bounds<Float>> borders = map.calcAxisBorders();
		float topOffset, leftOffset;
		topOffset = borders.y.start;
		leftOffset = borders.x.start;

		float mapX, mapY;
		mapX = x - leftOffset;
		mapY = y - topOffset;

		Coord<Float> mapSize = map.calcMapSize();
		float percentX, percentY;
		percentX = mapX / mapSize.x;
		percentY = mapY / mapSize.y;

		if (mapModel.viewOptions.yflip) percentY = 1.0f - percentY;

		int indexX = (int) Math.floor(mapModel.dataDimensions.x * percentX);
		int indexY = (int) Math.floor(mapModel.dataDimensions.y * percentY);

		return new Coord<Integer>(indexX, indexY);

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
		locX = (int) (leftOffset + (((float) coord.x / (float) mapModel.dataDimensions.x) * mapSize.x) - (MapDrawing
			.calcCellSize(mapSize.x, mapSize.y, mapModel.dr) * 0.5));
		locY = (int) (topOffset + (((float) coord.y / (float) mapModel.dataDimensions.y) * mapSize.y) - (MapDrawing
			.calcCellSize(mapSize.x, mapSize.y, mapModel.dr) * 0.5));

		return new Coord<Integer>(locX, locY);
	}


	// interpolation
	public void setInterpolation(int passes)
	{
		if (passes < 0) passes = 0;
		if (passes > 3) passes = 3;
		mapModel.viewOptions.interpolation = passes;
		invalidateInterpolation();
	}


	public int getInterpolation()
	{
		return mapModel.viewOptions.interpolation;
	}


	// data height and width
	public void setDataHeight(int height)
	{

		if (getDataWidth() * height > mapModel.mapSize()) height = mapModel.mapSize() / getDataWidth();
		if (height < 1) height = 1;

		// datamodel is the 'official' place for storing data dimensions
		mapModel.dataDimensions.y = height;
		mapModel.dr.dataHeight = height;
		invalidateInterpolation();
	}


	public int getDataHeight()
	{
		return mapModel.dataDimensions.y;
	}


	public void setDataWidth(int width)
	{

		if (getDataHeight() * width > mapModel.mapSize()) width = mapModel.mapSize() / getDataHeight();
		if (width < 1) width = 1;

		// datamodel is the 'official' place for storing data dimensions
		mapModel.dataDimensions.x = width;
		mapModel.dr.dataWidth = width;
		invalidateInterpolation();
	}


	public void invalidateInterpolation()
	{
		activeTabData.resultantData = null;
		updateListeners("");
	}


	public int getDataWidth()
	{
		return mapModel.dataDimensions.x;
	}


	// image height and width
	public void setImageHeight(float height)
	{
		mapModel.dr.imageHeight = height;
	}


	public float getImageHeight()
	{
		return mapModel.dr.imageHeight;
	}


	public void setImageWidth(float width)
	{
		mapModel.dr.imageWidth = width;
	}


	public float getImageWidth()
	{
		return mapModel.dr.imageWidth;
	}


	// contours
	public void setContours(boolean contours)
	{
		mapModel.viewOptions.contour = contours;
		updateListeners("");
	}


	public boolean getContours()
	{
		return mapModel.viewOptions.contour;
	}


	// spectrum
	public void setSpectrumSteps(int steps)
	{
		if (steps > 25) steps = 25;
		if (steps > 0)
		{
			mapModel.viewOptions.spectrumSteps = steps;
		}
		updateListeners("");
	}


	public int getSpectrumSteps()
	{
		return mapModel.viewOptions.spectrumSteps;
	}


	public void setMonochrome(boolean mono)
	{
		mapModel.viewOptions.monochrome = mono;
		updateListeners("");
	}


	public boolean getMonochrome()
	{
		return mapModel.viewOptions.monochrome;
	}


	public MapScaleMode getMapScaleMode()
	{
		return activeTabData.mapScaleMode;
	}


	public void setMapScaleMode(MapScaleMode mode)
	{
		activeTabData.mapScaleMode = mode;
		updateListeners("");
	}


	public MapDisplayMode getMapDisplayMode()
	{
		return activeTabData.displayMode;
	}


	public void setMapDisplayMode(MapDisplayMode mode)
	{
		activeTabData.displayMode = mode;
		invalidateInterpolation();
	}


	// set flip y axis - useful for other programmes that want to use this window.
	// default is to flip the y axis since that is the order in which the scan data
	// is taken
	public void setFlipY(boolean flip)
	{
		mapModel.viewOptions.yflip = flip;
		updateListeners("");
	}


	public boolean getFlipY()
	{
		return mapModel.viewOptions.yflip;
	}


	public void setShowSpectrum(boolean show)
	{
		mapModel.viewOptions.drawSpectrum = show;
		updateListeners("");
	}


	public boolean getShowSpectrum()
	{
		return mapModel.viewOptions.drawSpectrum;
	}


	public void setShowTitle(boolean show)
	{
		mapModel.viewOptions.drawTitle = show;
		updateListeners("");
	}


	public boolean getShowTitle()
	{
		return mapModel.viewOptions.drawTitle;
	}


	public void setShowDatasetTitle(boolean show)
	{
		mapModel.viewOptions.showDataSetTitle = show;
	}


	public boolean getShowDatasetTitle()
	{
		return mapModel.viewOptions.showDataSetTitle;
	}


	public void setShowCoords(boolean show)
	{
		mapModel.viewOptions.drawCoordinates = show;
		updateListeners("");
	}


	public boolean getShowCoords()
	{
		return mapModel.viewOptions.drawCoordinates;
	}


	private void generateFinalData()
	{

		List<Pair<TransitionSeries, Spectrum>> dataset = DataTypeFactory.<Pair<TransitionSeries, Spectrum>> list();
		Spectrum data = null;

		// Calculate the data to be shown based on the kind of display requested
		switch (activeTabData.displayMode)
		{
			case COMPOSITE:

				data = activeTabData.sumVisibleTransitionSeriesMaps();
				dataset.add(new Pair<TransitionSeries, Spectrum>(null, data));
				break;

			case OVERLAY:

				dataset = map(
						activeTabData.getVisibleTransitionSeries(),
						new FunctionMap<TransitionSeries, Pair<TransitionSeries, Spectrum>>() {


							public Pair<TransitionSeries, Spectrum> f(TransitionSeries ts)
							{
								return new Pair<TransitionSeries, Spectrum>(ts, activeTabData
									.getMapForTransitionSeries(ts));

							}
						});

				// data = activeTabData.sumVisibleTransitionSeriesMaps();
				break;

			case RATIO:

				// get transition series on ratio side 1
				List<TransitionSeries> side1 = activeTabData.getTransitionSeriesForRatioSide(1);
				// get transition series on ratio side 2
				List<TransitionSeries> side2 = activeTabData.getTransitionSeriesForRatioSide(2);

				// sum all of the maps for the given transition series for each side
				Spectrum side1Data = activeTabData.sumGivenTransitionSeriesMaps(side1);
				Spectrum side2Data = activeTabData.sumGivenTransitionSeriesMaps(side2);
				final float side1Min = SpectrumCalculations.min(side1Data, false);
				final float side2Min = SpectrumCalculations.min(side2Data, false);

				// compute the ratio of the two sides
				data = side1Data.zipWith(side2Data, new FunctionCombine<Float, Float, Float>() {


					public Float f(Float side1Value, Float side2Value)
					{

						if (side1Value <= 0.0 || side2Value <= 0.0) return 0.0f;

						float value = side1Value / side2Value;

						if (value < 1.0)
						{
							value = (1.0f / value);
							value = (float) (Math.log(value) / Math.log(Ratios.logValue));
							value = -value;
						}
						else
						{
							value = (float) (Math.log(value) / Math.log(Ratios.logValue));
						}

						return value;
					}
				});

				dataset.add(new Pair<TransitionSeries, Spectrum>(null, data));

				break;
		}

		// fix bad points, do interpolation, etc
		dataset = map(
				dataset,
				new FunctionMap<Pair<TransitionSeries, Spectrum>, Pair<TransitionSeries, Spectrum>>() {

					GridPerspective<Float>	grid	= new GridPerspective<Float>(
														mapModel.dataDimensions.x,
														mapModel.dataDimensions.y,
														0.0f);



					public Pair<TransitionSeries, Spectrum> f(Pair<TransitionSeries, Spectrum> map)
					{

						// fix bad points on the map
						Interpolation.interpolateBadPoints(grid, map.second, mapModel.badPoints);

						GridPerspective<Float> interpGrid = grid;

						Spectrum mapdata = map.second;

						// interpolation of data
						Pair<GridPerspective<Float>, Spectrum> interpolationResult;
						int count = 0;
						while (count < mapModel.viewOptions.interpolation)
						{
							interpolationResult = Interpolation.interpolateGridLinear(interpGrid, mapdata);
							interpGrid = interpolationResult.first;
							mapdata = interpolationResult.second;
							count++;
						}

						mapModel.interpolatedSize.x = interpGrid.width;
						mapModel.interpolatedSize.y = interpGrid.height;

						map.second = mapdata;
						return map;

					}
				});

		activeTabData.resultantData = dataset;
	}


	@Override
	protected void drawBackend(Surface backend, boolean vector)
	{

		generateFinalData();

		// Create the MapPainter objects which will draw the data as a map
		MapPainter mapPainter;
		List<AbstractPalette> paletteList;
		paletteList = DataTypeFactory.<AbstractPalette> list();

		final int spectrumSteps = (mapModel.viewOptions.contour) ? mapModel.viewOptions.spectrumSteps
				: Spectrums.DEFAULT_STEPS;
		AbstractPalette palette = new ThermalScalePalette(spectrumSteps, mapModel.viewOptions.monochrome);

		List<AxisPainter> axisPainters = DataTypeFactory.<AxisPainter> list();
		AxisPainter spectrumCoordPainter = null;

		// Map Dimensions
		int originalWidth = mapModel.dataDimensions.x;
		int originalHeight = mapModel.dataDimensions.y;

		mapModel.dr.dataWidth = mapModel.interpolatedSize.x;
		mapModel.dr.dataHeight = mapModel.interpolatedSize.y;

		if (mapModel.dimensionsProvided)
		{

			mapModel.viewOptions.bottomLeftCoord = new Coord<Number>(
				mapModel.realDimensions.x.start,
				mapModel.realDimensions.y.start);
			mapModel.viewOptions.bottomRightCoord = new Coord<Number>(
				mapModel.realDimensions.x.end,
				mapModel.realDimensions.y.start);
			mapModel.viewOptions.topRightCoord = new Coord<Number>(
				mapModel.realDimensions.x.end,
				mapModel.realDimensions.y.end);
			mapModel.viewOptions.topLeftCoord = new Coord<Number>(
				mapModel.realDimensions.x.start,
				mapModel.realDimensions.y.end);

		}
		else
		{

			mapModel.viewOptions.bottomLeftCoord = new Coord<Number>(1, 1);
			mapModel.viewOptions.bottomRightCoord = new Coord<Number>(originalWidth, 1);
			mapModel.viewOptions.topRightCoord = new Coord<Number>(originalWidth, originalHeight);
			mapModel.viewOptions.topLeftCoord = new Coord<Number>(1, originalHeight);

		}

		// Max Intensity Calculations
		switch (activeTabData.displayMode)
		{
			case COMPOSITE:

				if (activeTabData.mapScaleMode == MapScaleMode.VISIBLE_ELEMENTS)
				{
					mapModel.dr.maxYIntensity = SpectrumCalculations.max(activeTabData.resultantData.get(0).second);
				}
				else
				{
					mapModel.dr.maxYIntensity = SpectrumCalculations.max(activeTabData.sumAllTransitionSeriesMaps());
				}

				palette = new ThermalScalePalette(spectrumSteps, mapModel.viewOptions.monochrome);

				
				spectrumCoordPainter = new SpectrumCoordsAxisPainter(

					mapModel.viewOptions.drawCoordinates,
					mapModel.viewOptions.topLeftCoord,
					mapModel.viewOptions.topRightCoord,
					mapModel.viewOptions.bottomLeftCoord,
					mapModel.viewOptions.bottomRightCoord,
					mapModel.realDimensionsUnits,

					mapModel.viewOptions.drawSpectrum,
					mapModel.viewOptions.spectrumHeight,
					spectrumSteps,
					paletteList,

					mapModel.dimensionsProvided,
					"Intensity (counts)"

				);

				break;

			case OVERLAY:

				float redMax = fold(

				activeTabData.getTransitionSeriesForColour(OverlayColour.RED),
						0f,
						new FunctionCombine<Pair<TransitionSeries, Spectrum>, Float, Float>() {

							
							public Float f(Pair<TransitionSeries, Spectrum> mapdata, Float sum)
							{
								return sum + SpectrumCalculations.max(mapdata.second);
							}
						});

				float greenMax = fold(

				activeTabData.getTransitionSeriesForColour(OverlayColour.GREEN),
						0f,
						new FunctionCombine<Pair<TransitionSeries, Spectrum>, Float, Float>() {

							
							public Float f(Pair<TransitionSeries, Spectrum> mapdata, Float sum)
							{
								return sum + SpectrumCalculations.max(mapdata.second);
							}
						});

				float blueMax = fold(

				activeTabData.getTransitionSeriesForColour(OverlayColour.BLUE),
						0f,
						new FunctionCombine<Pair<TransitionSeries, Spectrum>, Float, Float>() {

							
							public Float f(Pair<TransitionSeries, Spectrum> mapdata, Float sum)
							{
								return sum + SpectrumCalculations.max(mapdata.second);
							}
						});

				mapModel.dr.maxYIntensity = Math.max(redMax, Math.max(greenMax, blueMax));

				palette = new ThermalScalePalette(spectrumSteps, mapModel.viewOptions.monochrome);

				spectrumCoordPainter = new LegendCoordsAxisPainter(

				mapModel.viewOptions.drawCoordinates,
					mapModel.viewOptions.topLeftCoord,
					mapModel.viewOptions.topRightCoord,
					mapModel.viewOptions.bottomLeftCoord,
					mapModel.viewOptions.bottomRightCoord,
					mapModel.realDimensionsUnits,

					mapModel.viewOptions.drawSpectrum,
					mapModel.viewOptions.spectrumHeight,

					mapModel.dimensionsProvided,
					"Colour",

					// create a list of color,string pairs for the legend by mapping the list of transitionseries per
					// colour and filter for empty strings
					filter(
							map(

							// input list - get a unique list of colours in use

									unique(activeTabData.overlayColour.values()),

									// mapping function - convert the color objects into color,string pairs (ie
									// color/element
									// list)
									new FunctionMap<OverlayColour, Pair<Color, String>>() {

										
										public Pair<Color, String> f(OverlayColour element)
										{
											// create a color,string pair
											return new Pair<Color, String>(

											element.toColor(),

											// fold the list of transition series using the concat operator
												foldr(

												// map the list of transitionSeries, list double pairs to just
														// transitionseries
														map(
																activeTabData.getTransitionSeriesForColour(element),
																Functions.<TransitionSeries, Spectrum> first()),
														"",
														new FunctionCombine<TransitionSeries, String, String>() {

															
															public String f(TransitionSeries ts, String title)
															{
																return title + (title.equals("") ? "" : ", ")
																		+ ts.toElementString();
															}
														})

											);
										}
									}),

							// filter for empty strings
							new FunctionMap<Pair<Color, String>, Boolean>() {

								
								public Boolean f(Pair<Color, String> element)
									{
										return !(element.second.length() == 0);
									}
							})

				);

				break;

			case RATIO:

				//create a unique list of the represented sides of the ratio from the set of visible TransitionSeries
				List<Integer> ratioSideValues = unique(map(activeTabData
					.getVisibleTransitionSeries(), new FunctionMap<TransitionSeries, Integer>() {

					
					public Integer f(TransitionSeries ts)
					{
						return activeTabData.ratioSide.get(ts);
					}
				}));
				//this is a valid ratio if there is at least 1 visible TS for each side
				boolean validRatio = (ratioSideValues.contains(1) && ratioSideValues.contains(2));

				//how many steps/markings will we display on the spectrum
				float steps = (float) Math.ceil(SpectrumCalculations.max(SpectrumCalculations
					.abs(activeTabData.resultantData.get(0).second)));
				mapModel.dr.maxYIntensity = steps;
				
				if (validRatio)
				{
					palette = new RatioPalette(spectrumSteps, mapModel.viewOptions.monochrome);
				}
				else
				{
					palette = new SingleColourPalette(Color.black);
				}


				List<Pair<Float, String>> spectrumMarkers = DataTypeFactory.<Pair<Float, String>> list();

				int increment = 1;
				if (steps > 8) increment = (int) Math.ceil(steps / 8);

				if (validRatio)
				{
					for (int i = -(int) steps; i <= (int) steps; i += increment)
					{
						float percent = 0.5f + 0.5f * (((float) i) / steps);

						/*
						 * int ratioValue = (int)Math.pow(10, Math.abs(i)); String ratio = ""; if (i < 0) ratio = "1:" +
						 * ratioValue; if (i > 0) ratio = ratioValue + ":1"; if (i == 0) ratio = "1:1";
						 */
						
						spectrumMarkers.add(new Pair<Float, String>(percent, Ratios.fromFloat(i, true)));
					}
				}

				spectrumCoordPainter = new SpectrumCoordsAxisPainter(

				mapModel.viewOptions.drawCoordinates,
					mapModel.viewOptions.topLeftCoord,
					mapModel.viewOptions.topRightCoord,
					mapModel.viewOptions.bottomLeftCoord,
					mapModel.viewOptions.bottomRightCoord,
					mapModel.realDimensionsUnits,

					mapModel.viewOptions.drawSpectrum,
					mapModel.viewOptions.spectrumHeight,
					spectrumSteps,
					paletteList,

					mapModel.dimensionsProvided,
					"Intensity (ratio)",
					activeTabData.displayMode == MapDisplayMode.RATIO,
					spectrumMarkers);

				break;

		}

		// Plot p = new Plot(activeTabModel.interpolatedData);
		// p.setAxisPainters(new LineAxisPainter(true, true, false, true));
		// ContainerAxisPainter linearPlot = new ContainerAxisPainter(p, 0.2d, ContainerAxisPainter.Side.BOTTOM);
		// axisPainters.add(linearPlot);

		if (mapModel.viewOptions.showDataSetTitle)
		{
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, datasetTitle, null));
		}

		if (mapModel.viewOptions.drawTitle)
		{
			String mapTitle = "";
			switch (activeTabData.displayMode)
			{
				case RATIO:

					mapTitle = activeTabData.mapLongTitle();
					break;

				case OVERLAY:

					mapTitle = "Overlay of " + activeTabData.mapLongTitle();

					break;

				case COMPOSITE:

					if (activeTabData.getVisibleTransitionSeries().size() > 1)
					{
						mapTitle = "Composite of " + activeTabData.mapLongTitle();
					}
					else
					{
						mapTitle = "Map of " + activeTabData.mapLongTitle();
					}

					break;
			}

			axisPainters.add(new TitleAxisPainter(1.0f, null, null, null, mapTitle));
		}

		axisPainters.add(spectrumCoordPainter);

		boolean oldVector = mapModel.dr.drawToVectorSurface;
		mapModel.dr.drawToVectorSurface = vector;

		map.setContext(backend);
		map.setAxisPainters(axisPainters);
		map.setDrawingRequest(mapModel.dr);

		switch (activeTabData.displayMode)
		{
			case COMPOSITE:

				paletteList.add(palette);
				mapPainter = MapTechniqueFactory.getTechnique(
						paletteList,
						activeTabData.resultantData.get(0).second,
						mapModel.viewOptions.contour,
						spectrumSteps);
				map.setPainters(mapPainter);
				map.draw();

				break;

			case RATIO:

				paletteList.add(palette);
				mapPainter = MapTechniqueFactory.getTechnique(
						paletteList,
						activeTabData.resultantData.get(0).second,
						false,
						spectrumSteps);
				map.setPainters(mapPainter);
				map.draw();

				break;

			case OVERLAY:

				// create a list of map painters, one for each of the maps we want to show
				List<MapPainter> painters = map(
						activeTabData.resultantData,
						new FunctionMap<Pair<TransitionSeries, Spectrum>, MapPainter>() {

							
							public MapPainter f(Pair<TransitionSeries, Spectrum> mapdata)
							{
								OverlayColour c = activeTabData.overlayColour.get(mapdata.first);

								MapPainter p = MapTechniqueFactory.getTechnique(new OverlayPalette(spectrumSteps, c
									.toColor()), mapdata.second, false, spectrumSteps);

								p.setCompositeMode(CompositeModes.ADD);

								return p;
							}
						});

				painters.add(0, new MapPainter(palette, null) {

					@Override
					public void drawElement(PainterData p)
					{

						p.context.setSource(Color.black);
						p.context.rectangle(0, 0, p.plotSize.x, p.plotSize.y);
						p.context.fill();
					}
				});

				// set the new data
				map.setPainters(painters);
				map.draw();

				break;
		}

		mapModel.dr.drawToVectorSurface = oldVector;

	}


	public void mapAsCSV(OutputStream os)
	{
		final OutputStreamWriter osw = new OutputStreamWriter(os);

		generateFinalData();

		final int width = mapModel.dataDimensions.x;
		FList<Pair<TransitionSeries, Spectrum>> maps = new FList<Pair<TransitionSeries, Spectrum>>(
			activeTabData.resultantData);



		maps.each(new FunctionEach<Pair<TransitionSeries, Spectrum>>() {


			public void f(Pair<TransitionSeries, Spectrum> element)
			{

				try
				{

					if (activeTabData.displayMode == MapDisplayMode.OVERLAY)
					{
						osw.write(element.first.toString() + "\n");
					}
					else if (activeTabData.displayMode == MapDisplayMode.RATIO)
					{
						osw.write(activeTabData.mapLongTitle() + " Ratio: Each value n represents " + Ratios.logValue
								+ "^n\n");
					}


					Spectrum s = element.second;
					FList<Spectrum> lines = new FList<Spectrum>();

					
					
					for (int i = 0; i < s.size(); i += width)
					{
						lines.add(s.subSpectrum(i, Math.min(i + width-1, s.size()-1)));
					}


					String scan = lines.showListBy(new FunctionMap<Spectrum, String>() {


						public String f(Spectrum list)
						{
							return Fn.map(list, Functions.<Float> show()).foldl(Functions.strcat(","));

						}
					}).foldl(Functions.strcat("\n"));

					osw.write(scan + "\n\n");

				}
				catch (IOException e)
				{
					e.printStackTrace();
				}

			}
		});


		try
		{
			osw.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	@Override
	public void setOutputIsPDF(boolean isPDF)
	{
		mapModel.dr.drawToVectorSurface = isPDF;
	}


	@Override
	public float getUsedHeight()
	{
		return map.calculateMapDimensions().y;
	}


	@Override
	public float getUsedWidth()
	{
		return map.calculateMapDimensions().x;
	}


	public boolean dimensionsProvided()
	{
		return mapModel.dimensionsProvided;
	}


	public void setNeedsRedraw()
	{
		map.needsMapRepaint();
	}


}
