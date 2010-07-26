package peakaboo.controller.mapper;



import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import fava.*;
import fava.datatypes.Bounds;
import fava.datatypes.Pair;
import fava.lists.FList;
import fava.signatures.FunctionCombine;
import fava.signatures.FunctionMap;
import static fava.Fn.*;

import peakaboo.calculations.Interpolation;
import peakaboo.controller.CanvasController;
import peakaboo.curvefit.peaktable.TransitionSeries;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.mapping.colours.OverlayColour;
import scidraw.drawing.backends.Surface;
import scidraw.drawing.backends.Surface.CompositeModes;
import scidraw.drawing.common.Spectrums;
import scidraw.drawing.map.MapDrawing;
import scidraw.drawing.map.painters.MapPainter;
import scidraw.drawing.map.painters.MapTechniqueFactory;
import scidraw.drawing.map.painters.ThreadedRasterMapPainter;
import scidraw.drawing.map.painters.axis.LegendCoordsAxisPainter;
import scidraw.drawing.map.painters.axis.SpectrumCoordsAxisPainter;
import scidraw.drawing.map.palettes.AbstractPalette;
import scidraw.drawing.map.palettes.OverlayPalette;
import scidraw.drawing.map.palettes.RatioPalette;
import scidraw.drawing.map.palettes.SaturationPalette;
import scidraw.drawing.map.palettes.SingleColourPalette;
import scidraw.drawing.map.palettes.ThermalScalePalette;
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


	private FunctionMap<Coord<Integer>, String> valueAtCoord;
	
	
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


	public String getIntensityMeasurementAtPoint(final Coord<Integer> mapCoord)
	{
		if (valueAtCoord == null) return "";
		return valueAtCoord.f(mapCoord);
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
		int side, newside;
		while (true) {
			
			side = (int)Math.sqrt( getDataHeight() * getDataWidth() );
			
			newside = (int)(side * Math.pow(2, passes));
		
			if (newside > 1500) {
				passes--;
			} else {
				break;
			}
		
		}

		
		if (passes < 0) passes = 0;
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
		
		setInterpolation(mapModel.viewOptions.interpolation);
		
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
		
		setInterpolation(mapModel.viewOptions.interpolation);
		
		invalidateInterpolation();
	}


	public void invalidateInterpolation()
	{
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


	
	private Pair<GridPerspective<Float>, Spectrum> interpolate(Spectrum data, GridPerspective<Float> grid, int passes)
	{
		
		GridPerspective<Float> interpGrid = grid;
		
		Spectrum mapdata = new Spectrum(data);
		
		Pair<GridPerspective<Float>, Spectrum> interpolationResult;
		int count = 0;
		while (count < passes)
		{
			interpolationResult = Interpolation.interpolateGridLinear(interpGrid, mapdata);
			interpGrid = interpolationResult.first;
			mapdata = interpolationResult.second;
			count++;
		}
		
		return new Pair<GridPerspective<Float>, Spectrum>(interpGrid, mapdata);
		
	}
	
	private Spectrum getCompositeMapData()
	{
		Spectrum data = activeTabData.sumVisibleTransitionSeriesMaps();
		
		GridPerspective<Float>	grid	= new GridPerspective<Float>(
				mapModel.dataDimensions.x,
				mapModel.dataDimensions.y,
				0.0f);
		
		// fix bad points on the map
		Interpolation.interpolateBadPoints(grid, data, mapModel.badPoints);
		
		// interpolation of data
		Pair<GridPerspective<Float>, Spectrum> interpolationResult = interpolate(data, grid, mapModel.viewOptions.interpolation);

		mapModel.interpolatedSize.x = interpolationResult.first.width;
		mapModel.interpolatedSize.y = interpolationResult.first.height;
		
		//data = mapdata;
		putValueFunctionForComposite(data);
		return interpolationResult.second;
		
		
	}
	
	
	private Map<OverlayColour, Spectrum> getOverlayMapData()
	{
		
		GridPerspective<Float>	grid	= new GridPerspective<Float>(
				mapModel.dataDimensions.x,
				mapModel.dataDimensions.y,
				0.0f);
		
		
		List<Pair<TransitionSeries, Spectrum>> dataset = map(
				activeTabData.getVisibleTransitionSeries(),
				new FunctionMap<TransitionSeries, Pair<TransitionSeries, Spectrum>>() {


					public Pair<TransitionSeries, Spectrum> f(TransitionSeries ts)
					{
						return new Pair<TransitionSeries, Spectrum>(ts, activeTabData
							.getMapForTransitionSeries(ts));

					}
				});
		
		/*
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
		
		*/
		

		Spectrum redSpectrum = null, greenSpectrum = null, blueSpectrum = null;
		Map<OverlayColour, Spectrum> uninterpolatedColours = DataTypeFactory.<OverlayColour, Spectrum>map();
		
		//get the TSs for this colour, and get their combined spectrum
		List<Spectrum> redSpectrums = Fn.filter(
			dataset, 
			new FunctionMap<Pair<TransitionSeries, Spectrum>, Boolean>() {

				public Boolean f(Pair<TransitionSeries, Spectrum> element)
				{
					return (activeTabData.overlayColour.get(element.first) == OverlayColour.RED);
				}
			}
		).map(Functions.<TransitionSeries, Spectrum>second());
		
		if (redSpectrums != null && redSpectrums.size() > 0) {
			redSpectrum = fold(
					redSpectrums,
					new FunctionCombine<Spectrum, Spectrum, Spectrum>() {

						public Spectrum f(Spectrum mapdata, Spectrum sum)
						{
							return SpectrumCalculations.addLists(mapdata, sum);
						}
					}
			);
			
			uninterpolatedColours.put(OverlayColour.RED, redSpectrum);
			Pair<GridPerspective<Float>, Spectrum> interpolationResult = interpolate(redSpectrum, grid, mapModel.viewOptions.interpolation);
			redSpectrum = interpolationResult.second;
			mapModel.interpolatedSize.x = interpolationResult.first.width;
			mapModel.interpolatedSize.y = interpolationResult.first.height;
			
			
		} else {
			redSpectrum = null;
		}
			
		
		//get the TSs for this colour, and get their combined spectrum
		List<Spectrum> greenSpectrums = Fn.filter(
			dataset, 
			new FunctionMap<Pair<TransitionSeries, Spectrum>, Boolean>() {

				public Boolean f(Pair<TransitionSeries, Spectrum> element)
				{
					return (activeTabData.overlayColour.get(element.first) == OverlayColour.GREEN);
				}
			}
		).map(Functions.<TransitionSeries, Spectrum>second());
		
		if (greenSpectrums != null && greenSpectrums.size() > 0){
			greenSpectrum = fold(
					greenSpectrums,
					new FunctionCombine<Spectrum, Spectrum, Spectrum>() {

						public Spectrum f(Spectrum mapdata, Spectrum sum)
						{
							return SpectrumCalculations.addLists(mapdata, sum);
						}
					}
			);
			
			uninterpolatedColours.put(OverlayColour.GREEN, greenSpectrum);
			Pair<GridPerspective<Float>, Spectrum> interpolationResult = interpolate(greenSpectrum, grid, mapModel.viewOptions.interpolation);
			greenSpectrum = interpolationResult.second;
			mapModel.interpolatedSize.x = interpolationResult.first.width;
			mapModel.interpolatedSize.y = interpolationResult.first.height;
			
		} else {
			greenSpectrum = null;
		}


			
		//get the TSs for this colour, and get their combined spectrum
		List<Spectrum> blueSpectrums = Fn.filter(
			dataset, 
			new FunctionMap<Pair<TransitionSeries, Spectrum>, Boolean>() {

				public Boolean f(Pair<TransitionSeries, Spectrum> element)
				{
					return (activeTabData.overlayColour.get(element.first) == OverlayColour.BLUE);
				}
			}
		).map(Functions.<TransitionSeries, Spectrum>second());
		
		if (blueSpectrums != null && blueSpectrums.size() > 0) {
			blueSpectrum = fold(
					blueSpectrums,
					new FunctionCombine<Spectrum, Spectrum, Spectrum>() {

						public Spectrum f(Spectrum mapdata, Spectrum sum)
						{
							return SpectrumCalculations.addLists(mapdata, sum);
						}
					}
			);
			
			uninterpolatedColours.put(OverlayColour.BLUE, blueSpectrum);
			Pair<GridPerspective<Float>, Spectrum> interpolationResult = interpolate(blueSpectrum, grid, mapModel.viewOptions.interpolation);
			blueSpectrum = interpolationResult.second;
			mapModel.interpolatedSize.x = interpolationResult.first.width;
			mapModel.interpolatedSize.y = interpolationResult.first.height;
					
		} else {
			blueSpectrum = null;
		}
			
		
		
		if (activeTabData.mapScaleMode == MapScaleMode.RELATIVE)
		{
			if (redSpectrum != null ) SpectrumCalculations.normalize_inplace(redSpectrum);
			if (greenSpectrum != null ) SpectrumCalculations.normalize_inplace(greenSpectrum);
			if (blueSpectrum != null ) SpectrumCalculations.normalize_inplace(blueSpectrum);
		}
		
		Map<OverlayColour, Spectrum> colours = DataTypeFactory.<OverlayColour, Spectrum>map();
		
		colours.put(OverlayColour.RED, redSpectrum);
		colours.put(OverlayColour.GREEN, greenSpectrum);
		colours.put(OverlayColour.BLUE, blueSpectrum);
		
		putValueFunctionForOverlay(uninterpolatedColours);
		return colours;
		
	}
	
	

	private Pair<Spectrum, Spectrum> getRatioMapData()
	{

		// get transition series on ratio side 1
		List<TransitionSeries> side1 = activeTabData.getTransitionSeriesForRatioSide(1);
		// get transition series on ratio side 2
		List<TransitionSeries> side2 = activeTabData.getTransitionSeriesForRatioSide(2);
		
		// sum all of the maps for the given transition series for each side
		Spectrum side1Data = activeTabData.sumGivenTransitionSeriesMaps(side1);
		Spectrum side2Data = activeTabData.sumGivenTransitionSeriesMaps(side2);
		
		if (activeTabData.mapScaleMode == MapScaleMode.RELATIVE)
		{
			SpectrumCalculations.normalize_inplace(side1Data);
			SpectrumCalculations.normalize_inplace(side2Data);
		}
				
		Spectrum ratioData = new Spectrum(side1Data.size());
		
		
		for (int i = 0; i < ratioData.size(); i++)
		{
			Float side1Value = side1Data.get(i);
			Float side2Value = side2Data.get(i);
			
			if (side1Value <= 0.0 || side2Value <= 0.0) {
				ratioData.set(i, Float.NaN);
				continue;
			}

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

			ratioData.set(i, value);
		}
		
		
		GridPerspective<Float>	grid	= new GridPerspective<Float>(
				mapModel.dataDimensions.x,
				mapModel.dataDimensions.y,
				0.0f);
		
		// fix bad points on the map
		Interpolation.interpolateBadPoints(grid, ratioData, mapModel.badPoints);
		
	
		Spectrum mapdata;
		
		Pair<GridPerspective<Float>, Spectrum> interpolationResult = interpolate(ratioData, grid, mapModel.viewOptions.interpolation);
		mapdata = interpolationResult.second;
		mapModel.interpolatedSize.x = interpolationResult.first.width;
		mapModel.interpolatedSize.y = interpolationResult.first.height;
		
		Spectrum invalidPoints = new Spectrum(ratioData.size(), 0f);
		for (int i = 0; i < ratioData.size(); i++)
		{
			if (  Float.isNaN(ratioData.get(i))  )
			{
				invalidPoints.set(i, 1f);
				ratioData.set(i, 0f);
			}
		}
		
		Spectrum invalidPointsInterpolated = new Spectrum(mapdata.size(), 0f);
		for (int i = 0; i < mapdata.size(); i++)
		{
			if (  Float.isNaN(mapdata.get(i))  )
			{
				invalidPointsInterpolated.set(i, 1f);
				mapdata.set(i, 0f);
			}
		}
				
		putValueFunctionForRatio(new Pair<Spectrum, Spectrum>(ratioData, invalidPoints));
		
		
		return new Pair<Spectrum, Spectrum>(mapdata, invalidPointsInterpolated);

		
	}
	
	
	/**
	 * sets the private object-scoped FunctionMap<Coord<Integer>, String> varialbe "valueAtCoord"
	 * to a function which reports values from the data passed in overlayData
	 * @param overlayData the overlay data to report on
	 */
	private void putValueFunctionForOverlay(final Map<OverlayColour, Spectrum> overlayData)
	{
		valueAtCoord = new FunctionMap<Coord<Integer>, String>() {

			public String f(Coord<Integer> coord)
			{
				
				if (activeTabData.mapScaleMode == MapScaleMode.RELATIVE) return "--";
				
				int index = getDataWidth() * coord.y + coord.x;
				
				FList<String> results = new FList<String>();
				
				for (OverlayColour c : OverlayColour.values())
				{
					if (overlayData.get(c) != null) results.add(  c.toString() + ": " + SigDigits.roundFloatTo(overlayData.get(c).get(index), 2)  );
				}
				return results.foldl(Functions.strcat(", "));
			}
		};
	}
	
	

	/**
	 * sets the private object-scoped FunctionMap<Coord<Integer>, String> varialbe "valueAtCoord"
	 * to a function which reports values from the data passed in ratioData
	 * @param ratioData the ratio data to report on
	 */
	private void putValueFunctionForRatio(final Pair<Spectrum, Spectrum> ratioData)
	{
		valueAtCoord = new FunctionMap<Coord<Integer>, String>() {

			public String f(Coord<Integer> coord)
			{
				
				if (activeTabData.mapScaleMode == MapScaleMode.RELATIVE) return "--";
				
				int index = getDataWidth() * coord.y + coord.x;
				if (ratioData.second.get(index) != 0) return "Invalid";
				return Ratios.fromFloat(  ratioData.first.get(index)  );
			}
		};
	}
	
	
	
	/**
	 * sets the private object-scoped FunctionMap<Coord<Integer>, String> varialbe "valueAtCoord"
	 * to a function which reports values from the data passed in 'data'
	 * @param data the data to report on
	 */
	private void putValueFunctionForComposite(final Spectrum data)
	{
		valueAtCoord = new FunctionMap<Coord<Integer>, String>() {

			public String f(Coord<Integer> coord)
			{
				int index = getDataWidth() * coord.y + coord.x;
				return "" + SigDigits.roundFloatTo(  data.get(index), 2  );
			}
		};
	}
	
	
	/*
	private void generateFinalData()
	{

		List<Pair<TransitionSeries, Spectrum>> dataset = DataTypeFactory.<Pair<TransitionSeries, Spectrum>> list();
		Spectrum data = null;
		List<Maybe<Float>> ratioData = null;

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
				
				break;

			case RATIO:

				// get transition series on ratio side 1
				List<TransitionSeries> side1 = activeTabData.getTransitionSeriesForRatioSide(1);
				// get transition series on ratio side 2
				List<TransitionSeries> side2 = activeTabData.getTransitionSeriesForRatioSide(2);

				// sum all of the maps for the given transition series for each side
				Spectrum side1Data = activeTabData.sumGivenTransitionSeriesMaps(side1);
				Spectrum side2Data = activeTabData.sumGivenTransitionSeriesMaps(side2);

				if (activeTabData.mapScaleMode == MapScaleMode.RELATIVE)
				{
					SpectrumCalculations.normalize_inplace(side1Data);
					SpectrumCalculations.normalize_inplace(side2Data);
				}
				
				// compute the ratio of the two sides
				ratioData = Fn.zipWith(new FList<Float>(side1Data), new FList<Float>(side2Data), new FunctionCombine<Float, Float, Maybe<Float>>() {


					public Maybe<Float> f(Float side1Value, Float side2Value)
					{

						if (side1Value <= 0.0 || side2Value <= 0.0) return new Maybe();

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

						return new Maybe(value);
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
*/

	/**
	 * Drawing logic for the composite view
	 * @param backend surface to draw to
	 * @param vector is this a vector-based backend
	 * @param spectrumSteps how many steps should our legeng spectrum have
	 */
	private void drawBackendComposite(Surface backend, boolean vector, int spectrumSteps)
	{
		
		AbstractPalette palette 			=		new ThermalScalePalette(spectrumSteps, mapModel.viewOptions.monochrome);
		AxisPainter spectrumCoordPainter 	= 		null;
		List<AbstractPalette> paletteList	=		DataTypeFactory.<AbstractPalette> list();
		List<AxisPainter> axisPainters 		= 		DataTypeFactory.<AxisPainter> list();
		MapPainter mapPainter;
		
		Spectrum data = getCompositeMapData();
		
		mapModel.dr.dataWidth = mapModel.interpolatedSize.x;
		mapModel.dr.dataHeight = mapModel.interpolatedSize.y;
		
		if (activeTabData.mapScaleMode == MapScaleMode.RELATIVE)
		{
			mapModel.dr.maxYIntensity = SpectrumCalculations.max(data);
		}
		else
		{
			mapModel.dr.maxYIntensity = SpectrumCalculations.max(activeTabData.sumAllTransitionSeriesMaps());
		}

		
		palette = new ThermalScalePalette(spectrumSteps, mapModel.viewOptions.monochrome);

		

		if (mapModel.viewOptions.showDataSetTitle)
		{
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, datasetTitle, null));
		}

		if (mapModel.viewOptions.drawTitle)
		{
			String mapTitle = "";

			if (activeTabData.getVisibleTransitionSeries().size() > 1)
			{
				mapTitle = "Composite of " + activeTabData.mapLongTitle();
			}
			else
			{
				mapTitle = "Map of " + activeTabData.mapLongTitle();
			}
			
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, null, mapTitle));
		}
		
		
		spectrumCoordPainter = new SpectrumCoordsAxisPainter
		(
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
		axisPainters.add(spectrumCoordPainter);

		
		boolean oldVector = mapModel.dr.drawToVectorSurface;
		mapModel.dr.drawToVectorSurface = vector;

		map.setContext(backend);
		map.setAxisPainters(axisPainters);
		map.setDrawingRequest(mapModel.dr);


		paletteList.add(palette);
		mapPainter = MapTechniqueFactory.getTechnique(
				paletteList,
				data,
				false,
				spectrumSteps);
		map.setPainters(mapPainter);
		map.draw();

		mapModel.dr.drawToVectorSurface = oldVector;

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
		List<AbstractPalette> paletteList	=		DataTypeFactory.<AbstractPalette> list();
		List<AxisPainter> axisPainters 		= 		DataTypeFactory.<AxisPainter> list();
		MapPainter mapPainter;
		
		Pair<Spectrum, Spectrum> ratiodata = getRatioMapData();
		
		mapModel.dr.dataWidth = mapModel.interpolatedSize.x;
		mapModel.dr.dataHeight = mapModel.interpolatedSize.y;
		
		
		//create a unique list of the represented sides of the ratio from the set of visible TransitionSeries
		List<Integer> ratioSideValues = unique(map(activeTabData.getVisibleTransitionSeries(), new FunctionMap<TransitionSeries, Integer>() {

			public Integer f(TransitionSeries ts)
			{
				return activeTabData.ratioSide.get(ts);
			}
		}));
		
		
		//this is a valid ratio if there is at least 1 visible TS for each side
		boolean validRatio = (ratioSideValues.contains(1) && ratioSideValues.contains(2));

		
		//how many steps/markings will we display on the spectrum
		float steps = (float) Math.ceil(SpectrumCalculations.max(SpectrumCalculations.abs(ratiodata.first)));
		mapModel.dr.maxYIntensity = steps;
		
		
		
		//if this is a valid ratio, make a real colour palette -- otherwise, just a black palette
		if (validRatio)
		{
			paletteList.add(new RatioPalette(spectrumSteps, mapModel.viewOptions.monochrome));
		}
		
		
		
		//generate a list of markers to be drawn along the spectrum to indicate the ratio at those points
		List<Pair<Float, String>> spectrumMarkers = DataTypeFactory.<Pair<Float, String>> list();

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
		if (mapModel.viewOptions.showDataSetTitle)
		{
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, datasetTitle, null));
		}

		//if we're map title, add a title axis painter to put a title on the bottom
		if (mapModel.viewOptions.drawTitle)
		{
			String mapTitle = "";
			mapTitle = activeTabData.mapLongTitle();
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, null, mapTitle));
		}
		

		//create a new coordinate/axis painter using the values in the model
		spectrumCoordPainter = new SpectrumCoordsAxisPainter
		(
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
			"Intensity (ratio)" + (activeTabData.mapScaleMode == MapScaleMode.RELATIVE ? " - Ratio sides scaled independently" : ""),
			activeTabData.displayMode == MapDisplayMode.RATIO,
			spectrumMarkers
		);
		axisPainters.add(spectrumCoordPainter);

		
		boolean oldVector = mapModel.dr.drawToVectorSurface;
		mapModel.dr.drawToVectorSurface = vector;

		map.setContext(backend);
		map.setAxisPainters(axisPainters);
		map.setDrawingRequest(mapModel.dr);


		
		
		
		mapPainter = MapTechniqueFactory.getTechnique(
				paletteList,
				ratiodata.first,
				false,
				spectrumSteps);
		
		Spectrum invalidPoints = ratiodata.second;
		final float datamax = mapModel.dr.maxYIntensity;
		
		
		invalidPoints.map_i(new FunctionMap<Float, Float>() {

			public Float f(Float value)
			{
				if (value == 1f) return datamax;
				return 0f;
			}});
		

		MapPainter invalidPainter = MapTechniqueFactory.getTechnique(new SaturationPalette(Color.gray, new Color(0,0,0,0)), invalidPoints, false, 0);
		
		
		
		map.setPainters(new FList<MapPainter>(mapPainter, invalidPainter));
		map.draw();

		
		mapModel.dr.drawToVectorSurface = oldVector;
		
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
		List<AxisPainter> axisPainters 		= 		DataTypeFactory.<AxisPainter> list();
		
		Map<OverlayColour, Spectrum> data = getOverlayMapData();
		
		mapModel.dr.dataWidth = mapModel.interpolatedSize.x;
		mapModel.dr.dataHeight = mapModel.interpolatedSize.y;
		
		
		Float redMax = 0f, greenMax = 0f, blueMax = 0f;
		
		Spectrum redSpectrum = data.get(OverlayColour.RED);
		Spectrum greenSpectrum = data.get(OverlayColour.GREEN);
		Spectrum blueSpectrum = data.get(OverlayColour.BLUE);
		
		
		if (redSpectrum != null ) redMax = SpectrumCalculations.max(redSpectrum);
		if (greenSpectrum != null ) greenMax = SpectrumCalculations.max(greenSpectrum);
		if (blueSpectrum != null ) blueMax = SpectrumCalculations.max(blueSpectrum);
		
		
		mapModel.dr.maxYIntensity = Math.max(redMax, Math.max(greenMax, blueMax));


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
			"Colour" + (activeTabData.mapScaleMode == MapScaleMode.RELATIVE ? " - Colours scaled independently" : ""),

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

								
								public Pair<Color, String> f(final OverlayColour ocolour)
								{
									// create a color,string pair
									return new Pair<Color, String>(

									ocolour.toColor(),

									// fold the list of transition series using the concat operator
										foldr(

												//grab a list of all TSs from the TS->Colour map and filter for the right colour
												filter(
													activeTabData.overlayColour.keySet(), 
													new FunctionMap<TransitionSeries, Boolean>() {
														public Boolean f(TransitionSeries ts)
														{
															return activeTabData.overlayColour.get(ts) == ocolour;
														}
													}
												),
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

			

		if (mapModel.viewOptions.showDataSetTitle)
		{
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, datasetTitle, null));
		}

		if (mapModel.viewOptions.drawTitle)
		{
			String mapTitle = "";
			mapTitle = "Overlay of " + activeTabData.mapLongTitle();
			axisPainters.add(new TitleAxisPainter(1.0f, null, null, null, mapTitle));
		}

		axisPainters.add(spectrumCoordPainter);

		boolean oldVector = mapModel.dr.drawToVectorSurface;
		mapModel.dr.drawToVectorSurface = vector;

		map.setContext(backend);
		map.setAxisPainters(axisPainters);
		map.setDrawingRequest(mapModel.dr);

	

		// create a list of map painters, one for each of the maps we want to show
		List<MapPainter> painters = DataTypeFactory.<MapPainter>list();
		MapPainter p;
		
		if (redSpectrum != null){
			p = MapTechniqueFactory.getTechnique(new OverlayPalette(spectrumSteps, OverlayColour.RED.toColor()), redSpectrum, false, spectrumSteps);
			p.setCompositeMode(CompositeModes.ADD);
			painters.add(p);
		}
			
		if (greenSpectrum != null) {
			p = MapTechniqueFactory.getTechnique(new OverlayPalette(spectrumSteps, OverlayColour.GREEN.toColor()), greenSpectrum, false, spectrumSteps);
			p.setCompositeMode(CompositeModes.ADD);
			painters.add(p);
		}
		
		if (blueSpectrum != null) {
			p = MapTechniqueFactory.getTechnique(new OverlayPalette(spectrumSteps, OverlayColour.BLUE.toColor()), blueSpectrum, false, spectrumSteps);
			p.setCompositeMode(CompositeModes.ADD);
			painters.add(p);
		}
		
		//need to paint the background black first
		painters.add(
				0, 
				new ThreadedRasterMapPainter(  new SingleColourPalette(Color.black), new Spectrum(mapModel.interpolatedSize.x * mapModel.interpolatedSize.y)  )
		);

		// set the new data
		map.setPainters(painters);
		map.draw();


		mapModel.dr.drawToVectorSurface = oldVector;
		
	}
	
	
	
	@Override
	protected void drawBackend(Surface backend, boolean vector)
	{

		// Map Dimensions
		int originalWidth = mapModel.dataDimensions.x;
		int originalHeight = mapModel.dataDimensions.y;

		

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
		
		
		final int spectrumSteps = (mapModel.viewOptions.contour) ? mapModel.viewOptions.spectrumSteps : Spectrums.DEFAULT_STEPS;
		
		switch (activeTabData.displayMode)
		{
			case COMPOSITE:
				drawBackendComposite(backend, vector, spectrumSteps);
				break;
				
			case OVERLAY:
				drawBackendOverlay(backend, vector, spectrumSteps);
				break;
				
			case RATIO:
				drawBackendRatio(backend, vector, spectrumSteps);
				break;
				
		}
		
		return;
		
		/*
		generateFinalData();

		// Create the MapPainter objects which will draw the data as a map
		MapPainter mapPainter;
		List<AbstractPalette> paletteList = DataTypeFactory.<AbstractPalette> list();

		
		AbstractPalette palette = new ThermalScalePalette(spectrumSteps, mapModel.viewOptions.monochrome);

		List<AxisPainter> axisPainters = DataTypeFactory.<AxisPainter> list();
		AxisPainter spectrumCoordPainter = null;



		
		mapModel.dr.dataWidth = mapModel.interpolatedSize.x;
		mapModel.dr.dataHeight = mapModel.interpolatedSize.y;

		// Max Intensity Calculations
		Spectrum greenSpectrum = null, blueSpectrum = null, redSpectrum = null;
		switch (activeTabData.displayMode)
		{
			case COMPOSITE:

				if (activeTabData.mapScaleMode == MapScaleMode.RELATIVE)
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

				float redMax = 0f;
				List<Spectrum> redSpectrums = Fn.map(
					activeTabData.getTransitionSeriesForColour(OverlayColour.RED), 
					Functions.<TransitionSeries, Spectrum>second()
				);
				if (redSpectrums.size() > 0) {
					redSpectrum = fold(
							redSpectrums,
							new FunctionCombine<Spectrum, Spectrum, Spectrum>() {
	
								public Spectrum f(Spectrum mapdata, Spectrum sum)
								{
									return SpectrumCalculations.addLists(mapdata, sum);
								}
							}
					);
				} else {
					redSpectrum = null;
				}
					
				
				float greenMax = 0f;
				List<Spectrum> greenSpectrums = Fn.map(
					activeTabData.getTransitionSeriesForColour(OverlayColour.GREEN), 
					Functions.<TransitionSeries, Spectrum>second()
				);
				if (greenSpectrums.size() > 0){
					greenSpectrum = fold(
							greenSpectrums,
							new FunctionCombine<Spectrum, Spectrum, Spectrum>() {
	
								public Spectrum f(Spectrum mapdata, Spectrum sum)
								{
									return SpectrumCalculations.addLists(mapdata, sum);
								}
							}
					);
				} else {
					greenSpectrum = null;
				}


					
				float blueMax = 0f;
				List<Spectrum> blueSpectrums = Fn.map(
					activeTabData.getTransitionSeriesForColour(OverlayColour.BLUE), 
					Functions.<TransitionSeries, Spectrum>second()
				);
				if (blueSpectrums.size() > 0) {
					blueSpectrum = fold(
							blueSpectrums,
							new FunctionCombine<Spectrum, Spectrum, Spectrum>() {
	
								public Spectrum f(Spectrum mapdata, Spectrum sum)
								{
									return SpectrumCalculations.addLists(mapdata, sum);
								}
							}
					);
				} else {
					blueSpectrum = null;
				}
					
				
				if (activeTabData.mapScaleMode == MapScaleMode.RELATIVE)
				{
					if (redSpectrum != null ) SpectrumCalculations.normalize_inplace(redSpectrum);
					if (greenSpectrum != null ) SpectrumCalculations.normalize_inplace(greenSpectrum);
					if (blueSpectrum != null ) SpectrumCalculations.normalize_inplace(blueSpectrum);
				}
				
				if (redSpectrum != null ) redMax = SpectrumCalculations.max(redSpectrum);
				if (greenSpectrum != null ) blueMax = SpectrumCalculations.max(greenSpectrum);
				if (blueSpectrum != null ) greenMax = SpectrumCalculations.max(blueSpectrum);
				
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
				List<MapPainter> painters = DataTypeFactory.<MapPainter>list();
				MapPainter p;
				
				if (redSpectrum != null){
					p = MapTechniqueFactory.getTechnique(new OverlayPalette(spectrumSteps, OverlayColour.RED.toColor()), redSpectrum, false, spectrumSteps);
					p.setCompositeMode(CompositeModes.ADD);
					painters.add(p);
				}
					
				if (greenSpectrum != null) {
					p = MapTechniqueFactory.getTechnique(new OverlayPalette(spectrumSteps, OverlayColour.GREEN.toColor()), greenSpectrum, false, spectrumSteps);
					p.setCompositeMode(CompositeModes.ADD);
					painters.add(p);
				}
				
				if (blueSpectrum != null) {
					p = MapTechniqueFactory.getTechnique(new OverlayPalette(spectrumSteps, OverlayColour.BLUE.toColor()), blueSpectrum, false, spectrumSteps);
					p.setCompositeMode(CompositeModes.ADD);
					painters.add(p);
				}
				

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
*/
	}


	/**
	 * Write the results of calling the object-scoped variable/function valueAtCoord.f(coord)
	 * with each coordinate on the map out to the provided outputstream
	 * @param os outputstream to write to
	 */
	public void mapAsCSV(OutputStream os)
	{
		final OutputStreamWriter osw = new OutputStreamWriter(os);

		//the getXXXXXXXXMapData methods have the side-effect of (re)placing
		//the valueAdCoord :: Coord<Integer> -> String  variable/function to reflect the values it calculates
		//we run these methods to ensure that the data and the function are correct
		switch (activeTabData.displayMode)
		{
			case COMPOSITE:
				getCompositeMapData();
				break;
				
			case OVERLAY:
				getOverlayMapData();
				break;
				
			case RATIO:
				getRatioMapData();
				break;
				
		}
		


		try {
			
		
			for (int y = 0; y < mapModel.dataDimensions.y; y++) {
				
				if (y != 0) osw.write("\n");
				
				for (int x = 0; x < mapModel.dataDimensions.x; x++) {
					
					if (x != 0) osw.write(", ");
					osw.write(valueAtCoord.f(new Coord<Integer>(x, y)));
					
				}
			}
			
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

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
