package org.peakaboo.controller.mapper.fitting;

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
import java.util.stream.Collectors;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.MappingController.UpdateType;
import org.peakaboo.controller.mapper.filtering.MapFilteringController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.MapScaleMode;
import org.peakaboo.display.map.modes.MapModes;
import org.peakaboo.display.map.modes.overlay.OverlayChannel;
import org.peakaboo.display.map.modes.overlay.OverlayColour;
import org.peakaboo.display.map.modes.scatter.ScatterMapMode;
import org.peakaboo.display.map.modes.scatter.ScatterMapMode.ScatterMapData;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.Ratios;
import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.framework.cyclops.SpectrumCalculations;
import org.peakaboo.framework.eventful.EventfulType;
import org.peakaboo.mapping.filter.Interpolation;
import org.peakaboo.mapping.filter.model.AreaMap;




public class MapFittingController extends EventfulType<String> {
	
	private MappingController map;
	
	private Function<Coord<Integer>, String> valueAtCoord;
	
	private Map<ITransitionSeries, Integer> ratioSide;
	private Map<ITransitionSeries, OverlayColour> overlayColour;
	private Map<ITransitionSeries, Boolean> compositeVisibility;
	private Map<ITransitionSeries, Integer> scatterSide;
	
	
	private MapModes displayMode;
	//TODO: should this be in MapSettingsController?
	private MapScaleMode mapScaleMode;
	
	
	
	
	public MapFittingController(MappingController map){
		this.map = map;
		
		displayMode = MapModes.COMPOSITE;
		mapScaleMode = MapScaleMode.ABSOLUTE;
		
		ratioSide = new HashMap<>();
		overlayColour = new HashMap<>();
		compositeVisibility = new HashMap<>();
		scatterSide = new HashMap<>();
		
		for (ITransitionSeries ts : map.rawDataController.getMapResultSet().getAllTransitionSeries()) {
			ratioSide.put(ts, 1);
			overlayColour.put(ts, OverlayColour.RED);
			compositeVisibility.put(ts, true);
			scatterSide.put(ts, 1);
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


	public MapModes getMapDisplayMode()
	{
		return this.displayMode;
	}



	public void setMapDisplayMode(MapModes mode)
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
				

		Map<OverlayColour, Spectrum> valueFunctionMaps = new HashMap<OverlayColour, Spectrum>();
		Map<OverlayColour, OverlayChannel> colourChannels = new HashMap<>();
		
		for (OverlayColour colour : OverlayColour.values()) {
			Spectrum colourSpectrum;
			//get the TSs for this colour, and get their combined spectrum
			List<Spectrum> colourSpectrums = dataset.stream()
					.filter(e -> (this.overlayColour.get(e.first) == colour))
					.map(e -> e.second)
					.collect(toList());

			List<ITransitionSeries> colourTS = dataset.stream()
					.filter(e -> (this.overlayColour.get(e.first) == colour))
					.map(e -> e.first)
					.collect(toList());
			
			if (colourSpectrums != null && colourSpectrums.size() > 0) {
				colourSpectrum = colourSpectrums.stream().reduce((a, b) -> SpectrumCalculations.addLists(a, b)).get();
				valueFunctionMaps.put(colour, colourSpectrum);
			} else {
				colourSpectrum = null;
			}
			
			if (this.mapScaleMode == MapScaleMode.RELATIVE && colourSpectrum != null) {
				SpectrumCalculations.normalize_inplace(colourSpectrum);
			}
			
			colourChannels.put(colour, new OverlayChannel(colourSpectrum, colourTS));
			
		}

		putValueFunctionForOverlay(valueFunctionMaps);
		return colourChannels;
		
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
	
	public ScatterMapData getScatterMapData() {
		
		
		// get transition series on ratio side 1
		List<ITransitionSeries> xTS = getTransitionSeriesForScatterSide(1);
		// get transition series on ratio side 2
		List<ITransitionSeries> yTS = getTransitionSeriesForScatterSide(2);
		
		// sum all of the maps for the given transition series for each side
		Spectrum xData = sumGivenTransitionSeriesMaps(xTS);
		Spectrum yData = sumGivenTransitionSeriesMaps(yTS);
		
		//Generate histograms
		List<Float> xSorted = xData.stream().collect(Collectors.toList());
		List<Float> ySorted = yData.stream().collect(Collectors.toList());
		xSorted.sort(Float::compare);
		ySorted.sort(Float::compare);
		int index999 = (int)(xSorted.size() * 0.999f);
		

		//float s1max = s1Data.max();
		//float s2max = s2Data.max();
		//mnax value is 95th percentile in histogram
		float xMax = xSorted.get(index999);
		float yMax = ySorted.get(index999);
		
		//if it's absolute, we use the larger max to scale both histograms
		if (this.mapScaleMode != MapScaleMode.RELATIVE) {
			xMax = Math.max(xMax, yMax);
			yMax = Math.max(xMax, yMax);
		}
		
		
		GridPerspective<Float> grid = new GridPerspective<Float>(ScatterMapMode.SCATTERSIZE, ScatterMapMode.SCATTERSIZE, 0f);
		Spectrum scatter = new ISpectrum(ScatterMapMode.SCATTERSIZE*ScatterMapMode.SCATTERSIZE);
		for (int i = 0; i < xData.size(); i++) {

			float xpct = xData.get(i) / xMax;
			float ypct = yData.get(i) / yMax;
			
			if (xpct <= 0.01f && ypct <= 0.01f) {
				/*
				 * Don't measure areas where neither element exists, this just creates a large
				 * spike at 0,0 which can drown out everything else
				 */
				continue;
			}
			
			int xbin = (int)(xpct*ScatterMapMode.SCATTERSIZE);
			int ybin = (int)(ypct*ScatterMapMode.SCATTERSIZE);
			if (xbin >= ScatterMapMode.SCATTERSIZE) { xbin = ScatterMapMode.SCATTERSIZE-1; }
			if (ybin >= ScatterMapMode.SCATTERSIZE) { ybin = ScatterMapMode.SCATTERSIZE-1; }
			
			grid.set(scatter, xbin, ybin, grid.get(scatter, xbin, ybin)+1);
			
		}

		//TODO: add putvaluefunction call
		//TODO: WHY IS THIS WRITTEN LIKE THIS?
		//putValueFunctionForRatio(new Pair<Spectrum, Spectrum>(ratioData, invalidPoints));
		
		ScatterMapData data = new ScatterMapData();
		data.data = scatter;
		data.xAxisTitle = getDatasetTitle(xTS);
		data.yAxisTitle = getDatasetTitle(yTS);
		data.xMaxCounts = xMax;
		data.yMaxCounts = yMax;
		
		
		return data;
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
				
			case SCATTER:
				getScatterMapData();
				break;
		}
		

		for (int y = 0; y < map.getFiltering().getFilteredDataHeight(); y++) {
			if (y != 0) sb.append("\n");
			
			for (int x = 0; x < map.getFiltering().getFilteredDataWidth(); x++) {
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
		
			case SCATTER:
				String axis1Title = getDatasetTitle(getTransitionSeriesForScatterSide(1));
				String axis2Title = getDatasetTitle(getTransitionSeriesForScatterSide(2));
				return "Scatter Plot of " + axis1Title + " : " + axis2Title;
				
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
		
		List<ITransitionSeries> tsList = this.compositeVisibility.keySet().stream().filter(a -> true).collect(toList());
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

	public List<ITransitionSeries> getTransitionSeriesForScatterSide(final int side)
	{
		return getVisibleTransitionSeries().stream().filter(e -> {
			Integer thisSide = this.scatterSide.get(e);
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
	
	
	public int getScatterSide(ITransitionSeries ts)
	{
		return this.scatterSide.get(ts);
	}
	public void setScatterSide(ITransitionSeries ts, int side)
	{
		this.scatterSide.put(ts, side);
		updateListeners(UpdateType.DATA_OPTIONS.toString());
	}
	
	/**
	 * Returns if this TransitionSeries is enabled, but returning false regardless
	 * of setting if {@link #getTransitionSeriesEnabled(ITransitionSeries)} returns
	 * false
	 */
	public synchronized boolean getTransitionSeriesVisibility(ITransitionSeries ts)
	{
		return this.compositeVisibility.get(ts) && getTransitionSeriesEnabled(ts);
	}
	public synchronized void setTransitionSeriesVisibility(ITransitionSeries ts, boolean visible)
	{
		this.compositeVisibility.put(ts, visible);
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
