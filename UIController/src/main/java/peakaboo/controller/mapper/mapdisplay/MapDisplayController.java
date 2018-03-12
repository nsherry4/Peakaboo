package peakaboo.controller.mapper.mapdisplay;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

import eventful.EventfulType;
import peakaboo.calculations.Interpolation;
import peakaboo.common.PeakabooLog;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.MappingController.UpdateType;
import peakaboo.curvefit.model.transitionseries.TransitionSeries;
import peakaboo.mapping.colours.OverlayColour;
import scitypes.Coord;
import scitypes.GridPerspective;
import scitypes.ISpectrum;
import scitypes.Pair;
import scitypes.Ratios;
import scitypes.SigDigits;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;


public class MapDisplayController extends EventfulType<String>
{
	
	
	private MapDisplaySettings 	tabModel;
	private MappingController 	map;
	private Function<Coord<Integer>, String> valueAtCoord;

	private AreaSelection areaSelection;
	private PointsSelection pointsSelection;
	
	
	public MapDisplayController(MappingController map)
	{
		
		this.map = map;
		
		tabModel = new MapDisplaySettings(map.mapsController.getMapResultSet().getAllTransitionSeries());
		
		//create selection models and pass their events along
		areaSelection = new AreaSelection(map);
		areaSelection.addListener(this::updateListeners);
		
		pointsSelection = new PointsSelection(map);
		pointsSelection.addListener(this::updateListeners);

		
	}


	public AreaSelection getAreaSelection() {
		return areaSelection;
	}

	

	public PointsSelection getPointsSelection() {
		return pointsSelection;
	}


	public MapScaleMode getMapScaleMode()
	{
		return tabModel.mapScaleMode;
	}


	public void setMapScaleMode(MapScaleMode mode)
	{
		tabModel.mapScaleMode = mode;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}

	public boolean isLogView() {
		return tabModel.logView;
	}
	public void setLogView(boolean logView) {
		tabModel.logView = logView;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}
	

	public MapDisplayMode getMapDisplayMode()
	{
		return tabModel.displayMode;
	}



	public void setMapDisplayMode(MapDisplayMode mode)
	{		
		tabModel.displayMode = mode;
		invalidateInterpolation();
	}
	
	
	

	public void invalidateInterpolation()
	{
		updateListeners(UpdateType.DATA_OPTIONS.toString());

	}
	

	public String getIntensityMeasurementAtPoint(final Coord<Integer> mapCoord)
	{
		if (valueAtCoord == null) return "";
		return valueAtCoord.apply(mapCoord);
	}
	
	

	public Spectrum getCompositeMapData()
	{
		Spectrum data = sumVisibleTransitionSeriesMaps();
		
		GridPerspective<Float>	grid	= new GridPerspective<Float>(
				map.settings.getDataWidth(),
				map.settings.getDataHeight(),
				0.0f);
		
		// fix bad points on the map
		Interpolation.interpolateBadPoints(grid, data, map.mapsController.getBadPoints());
		
		// interpolation of data
		Pair<GridPerspective<Float>, Spectrum> interpolationResult = interpolate(data, grid, map.settings.getInterpolation());

		
		//map.mapsController.interpolatedSize.x = interpolationResult.first.width;
		//mapModel.interpolatedSize.y = interpolationResult.first.height;
		
		//data = mapdata;
		putValueFunctionForComposite(data);
		return interpolationResult.second;
		
		
	}
	
	

	public Map<OverlayColour, Spectrum> getOverlayMapData()
	{
		
		GridPerspective<Float>	grid	= new GridPerspective<Float>(
				map.settings.getDataWidth(),
				map.settings.getDataHeight(),
				0.0f);
		
		
		List<Pair<TransitionSeries, Spectrum>> dataset = getVisibleTransitionSeries().stream()
				.map(ts -> new Pair<>(ts, getMapForTransitionSeries(ts)))
				.collect(toList());
				

		Spectrum redSpectrum = null, greenSpectrum = null, blueSpectrum = null, yellowSpectrum = null;
		Map<OverlayColour, Spectrum> uninterpolatedColours = new HashMap<OverlayColour, Spectrum>();
		
		//get the TSs for this colour, and get their combined spectrum
		List<Spectrum> redSpectrums = dataset.stream()
				.filter(e -> (tabModel.overlayColour.get(e.first) == OverlayColour.RED))
				.map(e -> e.second)
				.collect(toList());

		if (redSpectrums != null && redSpectrums.size() > 0) {
			redSpectrum = redSpectrums.stream().reduce((a, b) -> SpectrumCalculations.addLists(a, b)).get();
			
			uninterpolatedColours.put(OverlayColour.RED, redSpectrum);
			Pair<GridPerspective<Float>, Spectrum> interpolationResult = interpolate(redSpectrum, grid, map.settings.getInterpolation());
			redSpectrum = interpolationResult.second;
			//mapModel.interpolatedSize.x = interpolationResult.first.width;
			//mapModel.interpolatedSize.y = interpolationResult.first.height;
			
			
		} else {
			redSpectrum = null;
		}
			
		
		//get the TSs for this colour, and get their combined spectrum
		List<Spectrum> greenSpectrums = dataset.stream()
				.filter(e -> (tabModel.overlayColour.get(e.first) == OverlayColour.GREEN))
				.map(e -> e.second)
				.collect(toList());
		
		if (greenSpectrums != null && greenSpectrums.size() > 0){
			greenSpectrum = greenSpectrums.stream().reduce((a, b) -> SpectrumCalculations.addLists(a, b)).get();
			
			uninterpolatedColours.put(OverlayColour.GREEN, greenSpectrum);
			Pair<GridPerspective<Float>, Spectrum> interpolationResult = interpolate(greenSpectrum, grid, map.settings.getInterpolation());
			greenSpectrum = interpolationResult.second;
			//mapModel.interpolatedSize.x = interpolationResult.first.width;
			//mapModel.interpolatedSize.y = interpolationResult.first.height;
			
		} else {
			greenSpectrum = null;
		}


			
		//get the TSs for this colour, and get their combined spectrum
		List<Spectrum> blueSpectrums = dataset.stream()
				.filter(e -> tabModel.overlayColour.get(e.first) == OverlayColour.BLUE)
				.map(e -> e.second)
				.collect(toList());
		
		if (blueSpectrums != null && blueSpectrums.size() > 0) {
			blueSpectrum = blueSpectrums.stream().reduce((a, b) -> SpectrumCalculations.addLists(a, b)).get();
			
			uninterpolatedColours.put(OverlayColour.BLUE, blueSpectrum);
			Pair<GridPerspective<Float>, Spectrum> interpolationResult = interpolate(blueSpectrum, grid, map.settings.getInterpolation());
			blueSpectrum = interpolationResult.second;
			//mapModel.interpolatedSize.x = interpolationResult.first.width;
			//mapModel.interpolatedSize.y = interpolationResult.first.height;
					
		} else {
			blueSpectrum = null;
		}
		
		
		
		
		//get the TSs for this colour, and get their combined spectrum
		List<Spectrum> yellowSpectrums = dataset.stream()
				.filter(e -> tabModel.overlayColour.get(e.first) == OverlayColour.YELLOW)
				.map(e -> e.second)
				.collect(toList());
		
		if (yellowSpectrums != null && yellowSpectrums.size() > 0) {
			yellowSpectrum = yellowSpectrums.stream().reduce((a, b) -> SpectrumCalculations.addLists(a, b)).get();
			
			uninterpolatedColours.put(OverlayColour.YELLOW, yellowSpectrum);
			Pair<GridPerspective<Float>, Spectrum> interpolationResult = interpolate(yellowSpectrum, grid, map.settings.getInterpolation());
			yellowSpectrum = interpolationResult.second;
			//mapModel.interpolatedSize.x = interpolationResult.first.width;
			//mapModel.interpolatedSize.y = interpolationResult.first.height;
					
		} else {
			yellowSpectrum = null;
		}
			
		
		
		if (tabModel.mapScaleMode == MapScaleMode.RELATIVE)
		{
			if (redSpectrum != null ) SpectrumCalculations.normalize_inplace(redSpectrum);
			if (greenSpectrum != null ) SpectrumCalculations.normalize_inplace(greenSpectrum);
			if (blueSpectrum != null ) SpectrumCalculations.normalize_inplace(blueSpectrum);
			if (yellowSpectrum != null ) SpectrumCalculations.normalize_inplace(yellowSpectrum);
		}
		
		Map<OverlayColour, Spectrum> colours = new HashMap<OverlayColour, Spectrum>();
		
		colours.put(OverlayColour.RED, redSpectrum);
		colours.put(OverlayColour.GREEN, greenSpectrum);
		colours.put(OverlayColour.BLUE, blueSpectrum);
		colours.put(OverlayColour.YELLOW, yellowSpectrum);
		
		putValueFunctionForOverlay(uninterpolatedColours);
		return colours;
		
	}
	
	

	public Pair<Spectrum, Spectrum> getRatioMapData()
	{

		// get transition series on ratio side 1
		List<TransitionSeries> side1 = getTransitionSeriesForRatioSide(1);
		// get transition series on ratio side 2
		List<TransitionSeries> side2 = getTransitionSeriesForRatioSide(2);
		
		// sum all of the maps for the given transition series for each side
		Spectrum side1Data = sumGivenTransitionSeriesMaps(side1);
		Spectrum side2Data = sumGivenTransitionSeriesMaps(side2);
		
		if (tabModel.mapScaleMode == MapScaleMode.RELATIVE)
		{
			SpectrumCalculations.normalize_inplace(side1Data);
			SpectrumCalculations.normalize_inplace(side2Data);
		}
				
		Spectrum ratioData = new ISpectrum(side1Data.size());
		
		
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
				map.settings.getDataWidth(),
				map.settings.getDataHeight(),
				0.0f);
		
		// fix bad points on the map
		Interpolation.interpolateBadPoints(grid, ratioData, map.mapsController.getBadPoints());
		
	
		Spectrum mapdata;
		
		Pair<GridPerspective<Float>, Spectrum> interpolationResult = interpolate(ratioData, grid, map.settings.getInterpolation());
		mapdata = interpolationResult.second;
		//mapModel.interpolatedSize.x = interpolationResult.first.width;
		//mapModel.interpolatedSize.y = interpolationResult.first.height;
		
		Spectrum invalidPoints = new ISpectrum(ratioData.size(), 0f);
		for (int i = 0; i < ratioData.size(); i++)
		{
			if (  Float.isNaN(ratioData.get(i))  )
			{
				invalidPoints.set(i, 1f);
				ratioData.set(i, 0f);
			}
		}
		
		Spectrum invalidPointsInterpolated = new ISpectrum(mapdata.size(), 0f);
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
	 * sets the private object-scoped FunctionMap<Coord<Integer>, String> variable "valueAtCoord"
	 * to a function which reports values from the data passed in overlayData
	 * @param overlayData the overlay data to report on
	 */
	private void putValueFunctionForOverlay(final Map<OverlayColour, Spectrum> overlayData)
	{
		valueAtCoord = coord -> {
			
			if (tabModel.mapScaleMode == MapScaleMode.RELATIVE) return "--";
			
			int index = map.settings.getDataWidth() * coord.y + coord.x;
			List<String> results = new ArrayList<String>();
			for (OverlayColour c : OverlayColour.values())
			{
				if (overlayData.get(c) != null) results.add(  c.toString() + ": " + SigDigits.roundFloatTo(overlayData.get(c).get(index), 2)  );
			}
			return results.stream().collect(joining(", "));
		};
	}
	
	

	/**
	 * sets the private object-scoped FunctionMap<Coord<Integer>, String> varialbe "valueAtCoord"
	 * to a function which reports values from the data passed in ratioData
	 * @param ratioData the ratio data to report on
	 */
	private void putValueFunctionForRatio(final Pair<Spectrum, Spectrum> ratioData)
	{
		valueAtCoord = coord -> {
			if (tabModel.mapScaleMode == MapScaleMode.RELATIVE) return "--";
			
			int index = map.settings.getDataWidth() * coord.y + coord.x;
			if (ratioData.second.get(index) != 0) return "Invalid";
			return Ratios.fromFloat(  ratioData.first.get(index)  );
		};
	}
	
	
	
	/**
	 * sets the private object-scoped FunctionMap<Coord<Integer>, String> varialbe "valueAtCoord"
	 * to a function which reports values from the data passed in 'data'
	 * @param data the data to report on
	 */
	private void putValueFunctionForComposite(final Spectrum data)
	{
		valueAtCoord = coord -> {
			int index = map.settings.getDataWidth() * coord.y + coord.x;
			return "" + SigDigits.roundFloatTo(  data.get(index), 2  );
		};
	}
	

	
	private Pair<GridPerspective<Float>, Spectrum> interpolate(Spectrum data, GridPerspective<Float> grid, int passes)
	{
		
		GridPerspective<Float> interpGrid = grid;
		
		Spectrum mapdata = new ISpectrum(data);
		
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
	
	


	public String mapAsCSV()
	{
		StringBuilder sb = new StringBuilder();

		//the getXXXXXXXXMapData methods have the side-effect of (re)placing
		//the valueAdCoord :: Coord<Integer> -> String  variable/function to reflect the values it calculates
		//we run these methods to ensure that the data and the function are correct
		switch (tabModel.displayMode)
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
		

		for (int y = 0; y < map.settings.getDataHeight(); y++) {
			
			if (y != 0) sb.append("\n");
			
			for (int x = 0; x < map.settings.getDataWidth(); x++) {
				
				if (x != 0) sb.append(", ");
				sb.append(valueAtCoord.apply(new Coord<Integer>(x, y)));
				
			}
		}
			
		return sb.toString();

	}

	






	



	


	


	public String mapShortTitle(List<TransitionSeries> list){ return getShortDatasetTitle(list); }
	

	public String mapLongTitle(List<TransitionSeries> list){ return getDatasetTitle(list); }
	
	
	

	public String mapShortTitle(){ return getShortDatasetTitle(getVisibleTransitionSeries()); }
	

	public String mapLongTitle(){ 
	
		switch (tabModel.displayMode)
		{
			case RATIO:
				String side1Title = mapLongTitle(getTransitionSeriesForRatioSide(1));

				String side2Title = mapLongTitle(getTransitionSeriesForRatioSide(2));

				return "Map of " + side1Title + " : " + side2Title;
				
			default:
				
				return "Map of " + getDatasetTitle(getVisibleTransitionSeries());
				
		}
		
	}
	
	

	private String getDatasetTitle(List<TransitionSeries> list)
	{
		
		List<String> elementNames = list.stream().map(ts -> ts.toElementString()).collect(toList());
		String title = elementNames.stream().collect(joining(", "));
		if (title == null) return "-";
		return title;
		
	}
	

	private String getShortDatasetTitle(List<TransitionSeries> list)
	{
		
		Set<String> elementNames = list.stream().map(ts -> ts.element.toString()).collect(Collectors.toSet());
		String title = elementNames.stream().collect(joining(", "));
		
		if (title == null) return "-";
		return title;
		
	}
	
	
	


	public List<TransitionSeries> getAllTransitionSeries()
	{
		
		List<TransitionSeries> tsList = tabModel.visible.keySet().stream().filter(a -> true).collect(toList());
		Collections.sort(tsList);
		return tsList;
	}
	

	public List<TransitionSeries> getVisibleTransitionSeries()
	{
		return getAllTransitionSeries().stream().filter(e -> tabModel.visible.get(e)).collect(toList());
	}
	

	public Spectrum sumGivenTransitionSeriesMaps(List<TransitionSeries> list)
	{
		return map.mapsController.getMapResultSet().sumGivenTransitionSeriesMaps(list);
	}
	

	public Spectrum getMapForTransitionSeries(TransitionSeries ts)
	{
		List<TransitionSeries> tss = new ArrayList<TransitionSeries>();
		tss.add(ts);
		return map.mapsController.getMapResultSet().sumGivenTransitionSeriesMaps(tss);
	}
	

	public Spectrum sumVisibleTransitionSeriesMaps()
	{	
		return map.mapsController.getMapResultSet().sumGivenTransitionSeriesMaps(getVisibleTransitionSeries());
	}
	

	public Spectrum sumAllTransitionSeriesMaps()
	{		
		return map.mapsController.getMapResultSet().sumGivenTransitionSeriesMaps(tabModel.visible.keySet());
	}



	

	public List<TransitionSeries> getTransitionSeriesForRatioSide(final int side)
	{
		return getVisibleTransitionSeries().stream().filter(e -> {
			Integer thisSide = tabModel.ratioSide.get(e);
			return thisSide == side;
		}).collect(toList());
	}




	public OverlayColour getOverlayColour(TransitionSeries ts)
	{
		return tabModel.overlayColour.get(ts);
	}
	public void setOverlayColour(TransitionSeries ts, OverlayColour c)
	{
		tabModel.overlayColour.put(ts, c);
	}
	
	public Collection<OverlayColour> getOverlayColourValues()
	{
		return tabModel.overlayColour.values();
	}
	public Set<TransitionSeries> getOverlayColourKeys()
	{
		return tabModel.overlayColour.keySet();
	}
	

	public int getRatioSide(TransitionSeries ts)
	{
		return tabModel.ratioSide.get(ts);
	}
	public void setRatioSide(TransitionSeries ts, int side)
	{
		tabModel.ratioSide.put(ts, side);
	}
	
	public boolean getTransitionSeriesVisibility(TransitionSeries ts)
	{
		return tabModel.visible.get(ts);
	}
	public void setTransitionSeriesVisibility(TransitionSeries ts, boolean visible)
	{
		tabModel.visible.put(ts, visible);
	}







}
