package peakaboo.controller.mapper;



import java.awt.Color;
import java.util.List;

import peakaboo.calculations.Interpolation;
import peakaboo.calculations.SpectrumCalculations;
import peakaboo.controller.CanvasController;
import peakaboo.datatypes.Coord;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.GridPerspective;
import peakaboo.datatypes.Pair;
import peakaboo.datatypes.Range;
import peakaboo.datatypes.Ratios;
import peakaboo.datatypes.SigDigits;
import peakaboo.datatypes.Spectrum;
import peakaboo.datatypes.functional.Function1;
import peakaboo.datatypes.functional.Function2;
import peakaboo.datatypes.functional.Functional;
import peakaboo.datatypes.functional.stock.Functions;
import peakaboo.datatypes.peaktable.TransitionSeries;
import peakaboo.drawing.backends.Surface;
import peakaboo.drawing.backends.Surface.CompositeModes;
import peakaboo.drawing.common.Spectrums;
import peakaboo.drawing.map.MapDrawing;
import peakaboo.drawing.map.painters.MapPainter;
import peakaboo.drawing.map.painters.MapTechniqueFactory;
import peakaboo.drawing.map.painters.axis.LegendCoordsAxisPainter;
import peakaboo.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import peakaboo.drawing.map.palettes.AbstractPalette;
import peakaboo.drawing.map.palettes.OverlayPalette;
import peakaboo.drawing.map.palettes.RatioPalette;
import peakaboo.drawing.map.palettes.ThermalScalePalette;
import peakaboo.drawing.painters.PainterData;
import peakaboo.drawing.painters.axis.AxisPainter;
import peakaboo.drawing.painters.axis.TitleAxisPainter;
import peakaboo.mapping.colours.OverlayColor;



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
		updateListeners();
	}


	private List<Spectrum> getVisibleMapData()
	{
		if (activeTabData.resultantData == null)
		{
			generateFinalData();
		}

		return Functional.map(activeTabData.resultantData, Functions.<TransitionSeries, Spectrum> second());
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

				return Functional.foldr(

				Functional.map(getVisibleMapData(), new Function1<Spectrum, String>() {

					@Override
					public String f(Spectrum element)
					{
						return SigDigits.roundFloatTo(mapGrid.get(element, mapCoord.x, mapCoord.y), 2);
					}
				})

				, "", Functions.concat());

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

		updateListeners();

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

		Coord<Range<Float>> borders = map.calcAxisBorders();
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

		Coord<Range<Float>> borders = map.calcAxisBorders();
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
		updateListeners();
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
		updateListeners();
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
		updateListeners();
	}


	public int getSpectrumSteps()
	{
		return mapModel.viewOptions.spectrumSteps;
	}


	public void setMonochrome(boolean mono)
	{
		mapModel.viewOptions.monochrome = mono;
		updateListeners();
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
		updateListeners();
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
		updateListeners();
	}


	public boolean getFlipY()
	{
		return mapModel.viewOptions.yflip;
	}


	public void setShowSpectrum(boolean show)
	{
		mapModel.viewOptions.drawSpectrum = show;
		updateListeners();
	}


	public boolean getShowSpectrum()
	{
		return mapModel.viewOptions.drawSpectrum;
	}


	public void setShowTitle(boolean show)
	{
		mapModel.viewOptions.drawTitle = show;
		updateListeners();
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
		updateListeners();
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

				dataset = Functional.map(
						activeTabData.getVisibleTransitionSeries(),
						new Function1<TransitionSeries, Pair<TransitionSeries, Spectrum>>() {

							@Override
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
				data = side1Data.zipWith(side2Data, new Function2<Float, Float, Float>() {

					@Override
					public Float f(Float side1Value, Float side2Value)
					{

						if (side1Value == 0.0) side1Value = side1Min;
						if (side2Value == 0.0) side2Value = side2Min;

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
		dataset = Functional.map(
				dataset,
				new Function1<Pair<TransitionSeries, Spectrum>, Pair<TransitionSeries, Spectrum>>() {

					GridPerspective<Float>	grid	= new GridPerspective<Float>(
														mapModel.dataDimensions.x,
														mapModel.dataDimensions.y,
														0.0f);


					@Override
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

				float redMax = Functional.fold(

				activeTabData.getTransitionSeriesForColour(OverlayColor.RED),
						0f,
						new Function2<Pair<TransitionSeries, Spectrum>, Float, Float>() {

							@Override
							public Float f(Pair<TransitionSeries, Spectrum> mapdata, Float sum)
							{
								return sum + SpectrumCalculations.max(mapdata.second);
							}
						});

				float greenMax = Functional.fold(

				activeTabData.getTransitionSeriesForColour(OverlayColor.GREEN),
						0f,
						new Function2<Pair<TransitionSeries, Spectrum>, Float, Float>() {

							@Override
							public Float f(Pair<TransitionSeries, Spectrum> mapdata, Float sum)
							{
								return sum + SpectrumCalculations.max(mapdata.second);
							}
						});

				float blueMax = Functional.fold(

				activeTabData.getTransitionSeriesForColour(OverlayColor.BLUE),
						0f,
						new Function2<Pair<TransitionSeries, Spectrum>, Float, Float>() {

							@Override
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
					Functional.filter(
							Functional.map(

							// input list - get a unique list of colours in use

									Functional.unique(activeTabData.overlayColour.values()),

									// mapping function - convert the color objects into color,string pairs (ie
									// color/element
									// list)
									new Function1<OverlayColor, Pair<Color, String>>() {

										@Override
										public Pair<Color, String> f(OverlayColor element)
									{
										// create a color,string pair
										return new Pair<Color, String>(

										element.toColor(),

										// fold the list of transition series using the concat operator
											Functional.foldr(

											// map the list of transitionSeries, list double pairs to just
											// transitionseries
													Functional.map(
															activeTabData.getTransitionSeriesForColour(element),
															Functions.<TransitionSeries, Spectrum> first()),
													"",
													new Function2<TransitionSeries, String, String>() {

														@Override
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
							new Function1<Pair<Color, String>, Boolean>() {

								@Override
								public Boolean f(Pair<Color, String> element)
									{
										return !element.second.isEmpty();
									}
							})

				);

				break;

			case RATIO:

				List<Integer> ratioSideValues = Functional.unique(activeTabData.ratioSide.values());
				boolean validRatio = (ratioSideValues.contains(1) && ratioSideValues.contains(2));

				float steps = (float) Math.ceil(SpectrumCalculations.max(SpectrumCalculations
					.abs(activeTabData.resultantData.get(0).second)));

				mapModel.dr.maxYIntensity = steps;

				palette = new RatioPalette(spectrumSteps, mapModel.viewOptions.monochrome);

				List<Pair<Float, String>> spectrumMarkers = DataTypeFactory.<Pair<Float, String>> list();

				int increment = 1;
				if (steps > 100) increment = (int) Math.ceil(steps / 100);

				
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

					String side1Title = activeTabData.mapLongTitle(activeTabData.getTransitionSeriesForRatioSide(1));

					String side2Title = activeTabData.mapLongTitle(activeTabData.getTransitionSeriesForRatioSide(2));

					mapTitle = "← " + side1Title + " ∶ " + side2Title + " →";
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
				List<MapPainter> painters = Functional.map(
						activeTabData.resultantData,
						new Function1<Pair<TransitionSeries, Spectrum>, MapPainter>() {

							@Override
							public MapPainter f(Pair<TransitionSeries, Spectrum> mapdata)
							{
								OverlayColor c = activeTabData.overlayColour.get(mapdata.first);

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
