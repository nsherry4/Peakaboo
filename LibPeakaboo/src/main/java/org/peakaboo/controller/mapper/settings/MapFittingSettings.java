package org.peakaboo.controller.mapper.settings;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.MappingController.UpdateType;
import org.peakaboo.controller.mapper.filtering.MapFilteringController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.MapScaleMode;
import org.peakaboo.display.map.modes.MapDisplayMode;
import org.peakaboo.display.map.modes.OverlayChannel;
import org.peakaboo.display.map.modes.OverlayColour;
import org.peakaboo.mapping.filter.Interpolation;
import org.peakaboo.mapping.filter.model.AreaMap;

import cyclops.Coord;
import cyclops.GridPerspective;
import cyclops.ISpectrum;
import cyclops.Pair;
import cyclops.Ratios;
import cyclops.SigDigits;
import cyclops.Spectrum;
import cyclops.SpectrumCalculations;
import eventful.EventfulType;




public class MapFittingSettings extends EventfulType<String> {
	
	private MappingController map;
	
	private Function<Coord<Integer>, String> valueAtCoord;
	
	private Map<ITransitionSeries, Integer> ratioSide;
	private Map<ITransitionSeries, OverlayColour> overlayColour;
	private Map<ITransitionSeries, Boolean> visibility;
	
	private MapScaleMode mapScaleMode;
	
	private MapDisplayMode displayMode;
	private boolean logView;
	
	
	public MapFittingSettings(MappingController map){
		this.map = map;
		
		displayMode = MapDisplayMode.COMPOSITE;
		mapScaleMode = MapScaleMode.ABSOLUTE;
		
		ratioSide = new HashMap<>();
		overlayColour = new HashMap<>();
		visibility = new HashMap<>();
		
		for (ITransitionSeries ts : map.rawDataController.getMapResultSet().getAllTransitionSeries()) {
			ratioSide.put(ts, 1);
			overlayColour.put(ts, OverlayColour.RED);
			visibility.put(ts, true);
		}
		
	}
	
	

	public MapScaleMode getMapScaleMode()
	{
		return this.mapScaleMode;
	}


	public void setMapScaleMode(MapScaleMode mode)
	{
		this.mapScaleMode = mode;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}

	public boolean isLogView() {
		return this.logView;
	}
	public void setLogView(boolean logView) {
		this.logView = logView;
		updateListeners(UpdateType.UI_OPTIONS.toString());
	}
	

	public MapDisplayMode getMapDisplayMode()
	{
		return this.displayMode;
	}



	public void setMapDisplayMode(MapDisplayMode mode)
	{		
		this.displayMode = mode;
		updateListeners(UpdateType.DATA_OPTIONS.toString());
	}
	


	

	/*
	 * POST FILTERING
	 */
	public String getIntensityMeasurementAtPoint(final Coord<Integer> mapCoord)
	{
		if (valueAtCoord == null) return "";
		MapFilteringController filters = map.getFiltering();
		if (!filters.isValidPoint(mapCoord)) {
			return "";
		}
		return valueAtCoord.apply(mapCoord);
	}
	
	

	public Spectrum getCompositeMapData() {
		return getCompositeMapData(Optional.empty());
	}
	
	public Spectrum getCompositeMapData(Optional<ITransitionSeries> fitting)
	{
		
		Spectrum data;
		if (fitting.isPresent()) {
			data = getMapForTransitionSeries(fitting.get());
		} else {
			data = sumVisibleTransitionSeriesMaps();
		}
		
		GridPerspective<Float>	grid	= new GridPerspective<Float>(
				map.getUserDimensions().getUserDataWidth(),
				map.getUserDimensions().getUserDataHeight(),
				0.0f);
		
		// fix bad points on the map
		Interpolation.interpolateBadPoints(grid, data, map.rawDataController.getBadPoints());
		
		//data = mapdata;
		putValueFunctionForComposite(data);
		return data;
		
		
	}
	
	

	public Map<OverlayColour, OverlayChannel> getOverlayMapData()
	{
		
		GridPerspective<Float>	grid	= new GridPerspective<Float>(
				map.getUserDimensions().getUserDataWidth(),
				map.getUserDimensions().getUserDataHeight(),
				0.0f);
		
		
		List<Pair<ITransitionSeries, Spectrum>> dataset = getVisibleTransitionSeries().stream()
				.map(ts -> new Pair<>(ts, getMapForTransitionSeries(ts)))
				.collect(toList());
				

		Spectrum redSpectrum = null, greenSpectrum = null, blueSpectrum = null, yellowSpectrum = null;
		Map<OverlayColour, Spectrum> valueFunctionMaps = new HashMap<OverlayColour, Spectrum>();
		
		//get the TSs for this colour, and get their combined spectrum
		List<Spectrum> redSpectrums = dataset.stream()
				.filter(e -> (this.overlayColour.get(e.first) == OverlayColour.RED))
				.map(e -> e.second)
				.collect(toList());

		List<ITransitionSeries> redTS = dataset.stream()
				.filter(e -> (this.overlayColour.get(e.first) == OverlayColour.RED))
				.map(e -> e.first)
				.collect(toList());
		
		if (redSpectrums != null && redSpectrums.size() > 0) {
			redSpectrum = redSpectrums.stream().reduce((a, b) -> SpectrumCalculations.addLists(a, b)).get();
			valueFunctionMaps.put(OverlayColour.RED, redSpectrum);
		} else {
			redSpectrum = null;
		}
			
		
		//get the TSs for this colour, and get their combined spectrum
		List<Spectrum> greenSpectrums = dataset.stream()
				.filter(e -> (this.overlayColour.get(e.first) == OverlayColour.GREEN))
				.map(e -> e.second)
				.collect(toList());
		
		List<ITransitionSeries> greendTS = dataset.stream()
				.filter(e -> (this.overlayColour.get(e.first) == OverlayColour.GREEN))
				.map(e -> e.first)
				.collect(toList());
		
		if (greenSpectrums != null && greenSpectrums.size() > 0){
			greenSpectrum = greenSpectrums.stream().reduce((a, b) -> SpectrumCalculations.addLists(a, b)).get();
			valueFunctionMaps.put(OverlayColour.GREEN, greenSpectrum);
		} else {
			greenSpectrum = null;
		}


			
		//get the TSs for this colour, and get their combined spectrum
		List<Spectrum> blueSpectrums = dataset.stream()
				.filter(e -> this.overlayColour.get(e.first) == OverlayColour.BLUE)
				.map(e -> e.second)
				.collect(toList());
		
		List<ITransitionSeries> blueTS = dataset.stream()
				.filter(e -> (this.overlayColour.get(e.first) == OverlayColour.BLUE))
				.map(e -> e.first)
				.collect(toList());
		
		if (blueSpectrums != null && blueSpectrums.size() > 0) {
			blueSpectrum = blueSpectrums.stream().reduce((a, b) -> SpectrumCalculations.addLists(a, b)).get();
			valueFunctionMaps.put(OverlayColour.BLUE, blueSpectrum);	
		} else {
			blueSpectrum = null;
		}
		
		
		
		
		//get the TSs for this colour, and get their combined spectrum
		List<Spectrum> yellowSpectrums = dataset.stream()
				.filter(e -> this.overlayColour.get(e.first) == OverlayColour.YELLOW)
				.map(e -> e.second)
				.collect(toList());
		
		List<ITransitionSeries> yellowTS = dataset.stream()
				.filter(e -> (this.overlayColour.get(e.first) == OverlayColour.YELLOW))
				.map(e -> e.first)
				.collect(toList());
		
		if (yellowSpectrums != null && yellowSpectrums.size() > 0) {
			yellowSpectrum = yellowSpectrums.stream().reduce((a, b) -> SpectrumCalculations.addLists(a, b)).get();
			valueFunctionMaps.put(OverlayColour.YELLOW, yellowSpectrum);
		} else {
			yellowSpectrum = null;
		}
			
		
		
		if (this.mapScaleMode == MapScaleMode.RELATIVE)
		{
			if (redSpectrum != null ) SpectrumCalculations.normalize_inplace(redSpectrum);
			if (greenSpectrum != null ) SpectrumCalculations.normalize_inplace(greenSpectrum);
			if (blueSpectrum != null ) SpectrumCalculations.normalize_inplace(blueSpectrum);
			if (yellowSpectrum != null ) SpectrumCalculations.normalize_inplace(yellowSpectrum);
		}
		
		Map<OverlayColour, OverlayChannel> colours = new HashMap<>();
		
		colours.put(OverlayColour.RED, new OverlayChannel(redSpectrum, redTS));
		colours.put(OverlayColour.GREEN, new OverlayChannel(greenSpectrum, greendTS));
		colours.put(OverlayColour.BLUE, new OverlayChannel(blueSpectrum, blueTS));
		colours.put(OverlayColour.YELLOW, new OverlayChannel(yellowSpectrum, yellowTS));
		
		putValueFunctionForOverlay(valueFunctionMaps);
		return colours;
		
	}
	
	

	public Pair<Spectrum, Spectrum> getRatioMapData()
	{

		// get transition series on ratio side 1
		List<ITransitionSeries> side1 = getTransitionSeriesForRatioSide(1);
		// get transition series on ratio side 2
		List<ITransitionSeries> side2 = getTransitionSeriesForRatioSide(2);
		
		// sum all of the maps for the given transition series for each side
		Spectrum side1Data = sumGivenTransitionSeriesMaps(side1);
		Spectrum side2Data = sumGivenTransitionSeriesMaps(side2);
		
		if (this.mapScaleMode == MapScaleMode.RELATIVE)
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
				map.getUserDimensions().getUserDataWidth(),
				map.getUserDimensions().getUserDataHeight(),
				0.0f);
		
		// fix bad points on the map
		Interpolation.interpolateBadPoints(grid, ratioData, map.rawDataController.getBadPoints());
		

		Spectrum invalidPoints = new ISpectrum(ratioData.size(), 0f);
		for (int i = 0; i < ratioData.size(); i++)
		{
			if (  Float.isNaN(ratioData.get(i))  )
			{
				invalidPoints.set(i, 1f);
				ratioData.set(i, 0f);
			}
		}
		
				
		putValueFunctionForRatio(new Pair<Spectrum, Spectrum>(ratioData, invalidPoints));
		
		
		return new Pair<Spectrum, Spectrum>(ratioData, invalidPoints);

		
	}
	
	

	/**
	 * sets the private object-scoped FunctionMap<Coord<Integer>, String> variable "valueAtCoord"
	 * to a function which reports values from the data passed in overlayData
	 * @param overlayData the overlay data to report on
	 */
	private void putValueFunctionForOverlay(final Map<OverlayColour, Spectrum> overlayData)
	{
		valueAtCoord = coord -> {
			
			if (this.mapScaleMode == MapScaleMode.RELATIVE) return "--";
			
			int index = map.getFiltering().getFilteredDataWidth() * coord.y + coord.x;
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
			if (this.mapScaleMode == MapScaleMode.RELATIVE) return "--";
			
			int index = map.getFiltering().getFilteredDataWidth() * coord.y + coord.x;
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
			int index = map.getFiltering().getFilteredDataWidth() * coord.y + coord.x;
			if (index >= data.size()) return "";
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
		switch (this.displayMode)
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
		

		for (int y = 0; y < map.getUserDimensions().getUserDataHeight(); y++) {
			
			if (y != 0) sb.append("\n");
			
			for (int x = 0; x < map.getUserDimensions().getUserDataWidth(); x++) {
				
				if (x != 0) sb.append(", ");
				sb.append(valueAtCoord.apply(new Coord<Integer>(x, y)));
				
			}
		}
			
		return sb.toString();

	}

	






	



	


	


	
	

	


	public String mapLongTitle(){ 
	
		switch (this.displayMode)
		{
			case RATIO:
				String side1Title = getDatasetTitle(getTransitionSeriesForRatioSide(1));

				String side2Title = getDatasetTitle(getTransitionSeriesForRatioSide(2));

				return "Map of " + side1Title + " : " + side2Title;
			
			case OVERLAY:
				return "Overlay of " + getDatasetTitle(getVisibleTransitionSeries());				
		
			case COMPOSITE:
				if (getVisibleTransitionSeries().size() > 1) {
					return "Composite of " + getDatasetTitle(getVisibleTransitionSeries());
				} else {
					return "Map of " + getDatasetTitle(getVisibleTransitionSeries());
				}
		
			default:
				return "Map of " + getDatasetTitle(getVisibleTransitionSeries());
				
		}
		
	}	


	private String getDatasetTitle(List<ITransitionSeries> list)
	{
		
		List<String> elementNames = list.stream().map(ts -> ts.toString()).collect(toList());
		String title = elementNames.stream().collect(joining(", "));
		if (title == null) return "-";
		return title;
		
	}

	
	public synchronized List<ITransitionSeries> getAllTransitionSeries()
	{
		
		List<ITransitionSeries> tsList = this.visibility.keySet().stream().filter(a -> true).collect(toList());
		Collections.sort(tsList);
		return tsList;
	}
	

	public synchronized List<ITransitionSeries> getVisibleTransitionSeries()
	{
		List<ITransitionSeries> visible = new ArrayList<>();
		for (ITransitionSeries ts : getAllTransitionSeries()) {
			if (getTransitionSeriesVisibility(ts)) {
				visible.add(ts);
			}
		}
		return visible;
	}
	

	public Spectrum sumGivenTransitionSeriesMaps(List<ITransitionSeries> list)
	{
		int y = map.getFiltering().getFilteredDataHeight();
		int x = map.getFiltering().getFilteredDataWidth();
		
		
		//filter the maps
		List<AreaMap> filtereds = map.getFiltering().getAreaMaps(list);
		
		//merge the maps into a single composite map
		if (filtereds.isEmpty()) {
			//TODO: This may give a technically wrong result until interpolation is made into a filter
			return new ISpectrum(x * y);
		}
		
		return AreaMap.sumSpectrum(filtereds);
		
	}
	

	public Spectrum getMapForTransitionSeries(ITransitionSeries ts)
	{
		return sumGivenTransitionSeriesMaps(Collections.singletonList(ts));
	}
	

	public Spectrum sumVisibleTransitionSeriesMaps()
	{	
		return sumGivenTransitionSeriesMaps(getVisibleTransitionSeries());
	}
	

	public synchronized Spectrum sumAllTransitionSeriesMaps() {		
		
		AreaMap sum = map.getFiltering().getSummedMap();

		//When there are no maps, the sum will be null
		if (sum == null) {
			int y = map.getFiltering().getFilteredDataHeight();
			int x = map.getFiltering().getFilteredDataWidth();
			return new ISpectrum(x * y);
		}
		
		return new ISpectrum(sum.getData());
		
	}



	

	public List<ITransitionSeries> getTransitionSeriesForRatioSide(final int side)
	{
		return getVisibleTransitionSeries().stream().filter(e -> {
			Integer thisSide = this.ratioSide.get(e);
			return thisSide == side;
		}).collect(toList());
	}

	
	
	
	

	public OverlayColour getOverlayColour(ITransitionSeries ts)
	{
		return this.overlayColour.get(ts);
	}
	public void setOverlayColour(ITransitionSeries ts, OverlayColour c)
	{
		this.overlayColour.put(ts, c);
		updateListeners(UpdateType.DATA_OPTIONS.toString());
	}
	
	public Collection<OverlayColour> getOverlayColourValues()
	{
		return this.overlayColour.values();
	}
	public Set<ITransitionSeries> getOverlayColourKeys()
	{
		return this.overlayColour.keySet();
	}
	

	public int getRatioSide(ITransitionSeries ts)
	{
		return this.ratioSide.get(ts);
	}
	public void setRatioSide(ITransitionSeries ts, int side)
	{
		this.ratioSide.put(ts, side);
		updateListeners(UpdateType.DATA_OPTIONS.toString());
	}
	
	/**
	 * Returns if this TransitionSeries is enabled, but returning false regardless
	 * of setting if {@link #getTransitionSeriesEnabled(ITransitionSeries)} returns
	 * false
	 */
	public synchronized boolean getTransitionSeriesVisibility(ITransitionSeries ts)
	{
		return this.visibility.get(ts) && getTransitionSeriesEnabled(ts);
	}
	public synchronized void setTransitionSeriesVisibility(ITransitionSeries ts, boolean visible)
	{
		this.visibility.put(ts, visible);
		updateListeners(UpdateType.DATA_OPTIONS.toString());
	}

	/**
	 * Indicates if this TransitionSeries is enabled, or disabled (due to a lack of calibration, for example)
	 */
	public boolean getTransitionSeriesEnabled(ITransitionSeries ts) {
		if (getCalibrationProfile().isEmpty()) {
			return true;
		}
		return getCalibrationProfile().contains(ts);
	}
	


	public CalibrationProfile getCalibrationProfile() {
		return map.rawDataController.getCalibrationProfile();
	}



	public void setAllTransitionSeriesVisibility(boolean visible) {
		for (ITransitionSeries ts : getAllTransitionSeries()) {
			setTransitionSeriesVisibility(ts, visible);
		}
	}

	
}
