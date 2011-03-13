package peakaboo.mapping.results;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;



import fava.signatures.FnMap;
import static fava.Fn.*;

import peakaboo.curvefit.peaktable.TransitionSeries;
import scitypes.Spectrum;
import scitypes.SpectrumCalculations;

/**
 * 
 * This class stores a set of {@link MapResult}s, and provides convenience methods for setting pixel values,
 * visibility and generating map composites.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class MapResultSet implements Cloneable, Iterable<MapResult>
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
		
		maps = new ArrayList<MapResult>();
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
	
	@Override
	public MapResultSet clone()
	{
	
		List<MapResult> mapresults = new ArrayList<MapResult>();
		
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
	 * Generates a list of all of the TransitionSeries included in this MapResultSet
	 */
	public List<TransitionSeries> getAllTransitionSeries()
	{
		return map(maps, new FnMap<MapResult, TransitionSeries>() {
			
			public TransitionSeries f(MapResult mr) {
				return mr.transitionSeries;
			}
		});
	}
	
	/**
	 * 
	 * Places a value at a given index for the {@link MapResult} data associated with the given {@link TransitionSeries}
	 * 
	 * @param intensity the intensity value to place in the {@link MapResult} data
	 * @param ts the {@link TransitionSeries} associated with the desired {@link MapResult}
	 * @param index the index in the map data at which to place the new value
	 */
	public void putIntensityInMapAtPoint(float intensity, TransitionSeries ts, int index)
	{	
		
		MapResult m = getMap(ts);
		if (m == null) return;

		m.data.set(index, intensity);

	}

	/**
	 * 
	 * Composites the data in all {@link MapResult} into a single map. 
	 * 
	 * @return a list of double values representing the composited map
	 */
	public Spectrum sumAllTransitionSeriesMaps()
	{
		return sumGivenTransitionSeriesMaps(getAllTransitionSeries());

	}

	
	/**
	 * 
	 * Composites the data in all {@link MapResult} into a single map. 
	 * 
	 * @return a list of double values representing the composited map
	 */
	public Spectrum sumGivenTransitionSeriesMaps(Collection<TransitionSeries> list)
	{
		
		Spectrum sums = new Spectrum(maps.get(0).data.size(), 0.0f);

		for (MapResult map : maps) {
			if (list.contains(map.transitionSeries)) SpectrumCalculations.addLists_inplace(sums, map.data);
		}

		return sums;

	}

	public Iterator<MapResult> iterator()
	{
		return maps.iterator();
	}
	
}
