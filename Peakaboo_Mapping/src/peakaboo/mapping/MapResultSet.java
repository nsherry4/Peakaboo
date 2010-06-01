package peakaboo.mapping;


import java.util.List;

import peakaboo.calculations.ListCalculations;
import peakaboo.calculations.functional.Function1;
import peakaboo.calculations.functional.Function2;
import peakaboo.calculations.functional.Functional;
import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.peaktable.TransitionSeries;

/**
 * 
 * This class stores a set of {@link MapResult}s, and provides convenience methods for setting pixel values,
 * visibility and generating map composites.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class MapResultSet implements Cloneable
{

	private List<MapResult>	maps;
	private int				mapSize;


	/**
	 * 
	 * Create a new MapResultSet with MapResult objects for the given {@link TransitionSeries}, and of the given map size
	 *  
	 * @param transitionSeries list of {@link TransitionSeries} to store {@link MapResult}s for in this {@link MapResultSet}
	 * @param mapSize the size of the map data in each of the {@link MapResult}s
	 */
	public MapResultSet(List<TransitionSeries> transitionSeries, int mapSize)
	{
		maps = DataTypeFactory.<MapResult> list();
		for (TransitionSeries ts : transitionSeries) {
			maps.add(new MapResult(ts, mapSize));
		}
		this.mapSize = mapSize;

	}
	
	public MapResultSet(List<MapResult> maps, int mapSize, boolean flagToMakeSignatureDifferent)
	{
		this.maps = maps;
		this.mapSize = mapSize;
	}
	
	public MapResultSet clone()
	{
	
		List<MapResult> mapresults = DataTypeFactory.<MapResult>list();
		
		for (MapResult map : maps)
		{
			mapresults.add(map.clone());
		}
		
		return new MapResultSet(mapresults, mapSize, true);
		
	}
	
	
	/**
	 * Get the size of a map
	 * @return the size of a map
	 */
	public int size()
	{
		return mapSize;
	}


	/**
	 * Get the number of maps
	 * @return the number of maps
	 */
	public int mapCount()
	{
		return maps.size();
	}


	/**
	 * 
	 * Get a {@link MapResult} at a specific index. Useful for UI lists.
	 * 
	 * @param index index of the map to retrieve
	 * @return the {@link MapResult} at the given index
	 */
	public MapResult getMap(int index)
	{
		return maps.get(index);
	}


	/**
	 * 
	 * Gets the {@link MapResult} related to the given {@link TransitionSeries}
	 * 
	 * @param ts the {@link TransitionSeries} to look up the {@link MapResult} with
	 * @return the {@link MapResult} for the given {@link TransitionSeries}
	 */
	private MapResult getMap(TransitionSeries ts)
	{
		for (MapResult m : maps) {
			if (m.transitionSeries == ts) return m;
		}
		return null;
	}
	

	/**
	 * 
	 * Places a value at a given index for the {@link MapResult} data associated with the given {@link TransitionSeries}
	 * 
	 * @param intensity the intensity value to place in the {@link MapResult} data
	 * @param ts the {@link TransitionSeries} associated with the desired {@link MapResult}
	 * @param index the index in the map data at which to place the new value
	 */
	public void putIntensityInMapAtPoint(double intensity, TransitionSeries ts, int index)
	{

		MapResult m = getMap(ts);
		if (m == null) return;

		m.data.set(index, intensity);

	}

	/**
	 * 
	 * Toggles the visiblity of the {@link MapResult} associated with the given {@link TransitionSeries}
	 * 
	 * @param ts the {@link TransitionSeries} associated with the desired {@link MapResult}
	 * @param visible the desired visibility of the {@link MapResult} in question
	 */
	public void setMapVisible(TransitionSeries ts, boolean visible)
	{
		MapResult m = getMap(ts);
		m.visible = visible;
	}


	/**
	 * 
	 * Returns the visibility of the {@link MapResult} associated with the given {@link TransitionSeries}
	 * 
	 * @param ts the {@link TransitionSeries} associated with the desired {@link MapResult}
	 * @return the visibiltiy of the desired {@link MapResult}
	 */
	public boolean getMapVisible(TransitionSeries ts)
	{
		return getMap(ts).visible;
	}


	/**
	 * 
	 * Composites the data in all visible {@link MapResult} into a single map. 
	 * 
	 * @return a list of double values representing the composited map
	 */
	public List<Double> sumVisibleTransitionSeriesMaps()
	{
		List<Double> sums = DataTypeFactory.<Double> list();
		for (int i = 0; i < mapSize; i++) {
			sums.add(0.0);
		}

		for (MapResult map : maps) {
			if (map.visible) ListCalculations.addLists_inplace(sums, map.data);
		}

		return sums;

	}


	/**
	 * 
	 * Composites the data in all {@link MapResult} into a single map. 
	 * 
	 * @return a list of double values representing the composited map
	 */
	public List<Double> sumAllTransitionSeriesMaps()
	{
		List<Double> sums = DataTypeFactory.<Double> list();
		for (int i = 0; i < mapSize; i++) {
			sums.add(0.0);
		}

		for (MapResult map : maps) {
			ListCalculations.addLists_inplace(sums, map.data);
		}

		return sums;

	}


	
	public String getDatasetTitle(String separator)
	{
		if (separator == null) separator = ", ";
		

		List<MapResult> visibleMaps = Functional.filter(maps, new Function1<MapResult, Boolean>() {
			
			public Boolean run(MapResult map) {
				return map.visible;
			}
		});
		
		List<String> elementNames = Functional.map(visibleMaps, new Function1<MapResult, String>() {
			
			public String run(MapResult map) {
				return map.transitionSeries.toElementString();
			}
		});

		String title = Functional.foldr(elementNames, new Function2<String, String, String>() {
			
			public String run(String elementName, String title) {
				return title + ", " + elementName;
			}
		});
		
		if (title == null) return "-";
		return title;
		
	}
	

	public String getShortDatasetTitle(String separator)
	{
		if (separator == null) separator = ", ";
		
		
		List<MapResult> visibleMaps = Functional.filter(maps, new Function1<MapResult, Boolean>() {
			
			public Boolean run(MapResult map) {
				return map.visible;
			}
		});
		
		List<String> elementNames = Functional.map(visibleMaps, new Function1<MapResult, String>() {
			
			public String run(MapResult map) {
				return map.transitionSeries.element.toString();
			}
		});
		
		//trim out the duplicated
		elementNames = Functional.unique(elementNames, new Function2<String, String, Boolean>() {
			
			public Boolean run(String elem1, String elem2) {
				return elem1.equals(elem2);
			}
		});

		String title = Functional.foldr(elementNames, new Function2<String, String, String>() {
			
			public String run(String elementName, String title) {
				return title + ", " + elementName;
			}
		});
		
		if (title == null) return "-";
		return title;
		
	}
	
	
}
