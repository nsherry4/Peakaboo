package peakaboo.mapping.results;


import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import cyclops.ISpectrum;
import cyclops.Spectrum;
import cyclops.SpectrumCalculations;
import peakaboo.calibration.CalibrationProfile;
import peakaboo.curvefit.peak.transition.ITransitionSeries;


/**
 * 
 * This class stores a set of {@link AreaMap}s, and provides convenience methods for setting pixel values,
 * visibility and generating map composites.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class AreaMapSet implements Cloneable
{

	private List<AreaMap>	maps;
	private int				mapSize;


	/**
	 * 
	 * Create a new AreaMapSet with AreaMap objects for the given {@link ITransitionSeries}, and of the given map size
	 *  
	 * @param transitionSeries list of {@link ITransitionSeries} to store {@link AreaMap}s for in this {@link AreaMapSet}
	 * @param mapSize the size of the map data in each of the {@link AreaMap}s
	 */
	public AreaMapSet(List<ITransitionSeries> transitionSeries, int mapSize)
	{
		maps = new ArrayList<AreaMap>();
		for (ITransitionSeries ts : transitionSeries) {
			maps.add(new AreaMap(ts, mapSize));
		}
		this.mapSize = mapSize;

	}
	
	public AreaMapSet(List<AreaMap> maps, int mapSize, boolean flagToMakeSignatureDifferent)
	{
		this.maps = maps;
		this.mapSize = mapSize;
	}
	
	@Override
	public AreaMapSet clone() throws CloneNotSupportedException
	{
			
		List<AreaMap> areamaps = new ArrayList<AreaMap>();
		
		for (AreaMap map : maps)
		{
			areamaps.add(map.clone());
		}
				
		return new AreaMapSet(areamaps, mapSize, true);
		
	}
	
	
	public Stream<AreaMap> stream() {
		return maps.stream();
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
	 * Get a {@link AreaMap} at a specific index. Useful for UI lists.
	 * 
	 * @param index index of the map to retrieve
	 * @return the {@link AreaMap} at the given index
	 */
	public AreaMap getMap(int index)
	{
		return maps.get(index);
	}


	/**
	 * 
	 * Gets the {@link AreaMap} related to the given {@link ITransitionSeries}
	 * 
	 * @param ts the {@link ITransitionSeries} to look up the {@link AreaMap} with
	 * @return the {@link AreaMap} for the given {@link ITransitionSeries}
	 */
	public AreaMap getMap(ITransitionSeries ts)
	{
		for (AreaMap m : maps) {
			if (m.transitionSeries == ts) return m;
		}
		return null;
	}
	

	/**
	 * Generates a list of all of the TransitionSeries included in this AreaMapSet
	 */
	public List<ITransitionSeries> getAllTransitionSeries()
	{
		return maps.stream().map(am -> am.transitionSeries).collect(toList());
	}
	
	/**
	 * 
	 * Places a value at a given index for the {@link AreaMap} data associated
	 * with the given {@link ITransitionSeries}. This method will apply the
	 * {@link CalibrationProfile} to the added values.
	 * 
	 * @param intensity the intensity value to place in the {@link AreaMap} data
	 * @param ts        the {@link ITransitionSeries} associated with the desired
	 *                  {@link AreaMap}
	 * @param index     the index in the map data at which to place the new value
	 */
	public void putIntensityInMapAtPoint(float intensity, ITransitionSeries ts, int index)
	{	
		
		
		AreaMap m = getMap(ts);
		if (m == null) return;

		m.setData(index, intensity);

	}

	/**
	 * 
	 * Composites the data in all {@link AreaMap} into a single map. 
	 * 
	 * @return a list of double values representing the composited map
	 */
	public Spectrum sumAllTransitionSeriesMaps(CalibrationProfile profile)
	{
		return sumGivenTransitionSeriesMaps(getAllTransitionSeries(), profile);
	}

	
	/**
	 * 
	 * Composites the data in all {@link AreaMap} into a single map. 
	 * 
	 * @return a list of double values representing the composited map
	 */
	public Spectrum sumGivenTransitionSeriesMaps(Collection<ITransitionSeries> list, CalibrationProfile profile)
	{
		
		Spectrum sums = new ISpectrum(maps.get(0).size(), 0.0f);

		for (AreaMap map : maps) {
			if (list.contains(map.transitionSeries)) SpectrumCalculations.addLists_inplace(sums, map.getData(profile));
		}

		return sums;

	}

	public Iterator<AreaMap> iterator()
	{
		return maps.iterator();
	}
	
	
}
