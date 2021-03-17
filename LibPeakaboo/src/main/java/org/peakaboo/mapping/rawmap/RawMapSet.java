package org.peakaboo.mapping.rawmap;


import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.spectrum.ISpectrum;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;
import org.peakaboo.framework.cyclops.spectrum.SpectrumCalculations;


/**
 * 
 * This class stores a set of {@link RawMap}s, and provides convenience methods for setting pixel values,
 * visibility and generating map composites.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class RawMapSet implements Cloneable, Iterable<RawMap> {

	private List<RawMap>	maps;
	private int				mapSize;
	
	//Are all points valid, or does this map contain dummy points (eg for non-rectangular datasets)
	private boolean			allPointsValid;


	/**
	 * 
	 * Create a new MapResultSet with MapResult objects for the given {@link ITransitionSeries}, and of the given map size
	 *  
	 * @param transitionSeries list of {@link ITransitionSeries} to store {@link RawMap}s for in this {@link RawMapSet}
	 * @param mapSize the size of the map data in each of the {@link RawMap}s
	 */
	public RawMapSet(List<ITransitionSeries> transitionSeries, int mapSize, boolean allPointsValid)
	{
		maps = new ArrayList<>();
		for (ITransitionSeries ts : transitionSeries) {
			maps.add(new RawMap(ts, mapSize));
		}
		this.mapSize = mapSize;
		this.allPointsValid = allPointsValid;

	}
	
	public RawMapSet(List<RawMap> maps, int mapSize, boolean allPointsValid, boolean flagToMakeSignatureDifferent)
	{
		this.maps = maps;
		this.mapSize = mapSize;
		this.allPointsValid = allPointsValid;
	}
	
	@Override
	public RawMapSet clone() throws CloneNotSupportedException
	{
			
		List<RawMap> mapresults = new ArrayList<>();
		
		for (RawMap map : maps)
		{
			mapresults.add(map.clone());
		}
				
		return new RawMapSet(mapresults, mapSize, allPointsValid, true);
		
	}
	
	
	public Stream<RawMap> stream() {
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
	 * Get a {@link RawMap} at a specific index. Useful for UI lists.
	 * 
	 * @param index index of the map to retrieve
	 * @return the {@link RawMap} at the given index
	 */
	public RawMap getMap(int index)
	{
		return maps.get(index);
	}


	/**
	 * 
	 * Gets the {@link RawMap} related to the given {@link ITransitionSeries}
	 * 
	 * @param ts the {@link ITransitionSeries} to look up the {@link RawMap} with
	 * @return the {@link RawMap} for the given {@link ITransitionSeries}
	 */
	public RawMap getMap(ITransitionSeries ts)
	{
		for (RawMap m : maps) {
			if (m.transitionSeries == ts) return m;
		}
		return null;
	}
	

	public List<RawMap> getMaps() {
		return new ArrayList<>(maps);
	}
	
	/**
	 * Generates a list of all of the TransitionSeries included in this MapResultSet
	 */
	public List<ITransitionSeries> getAllTransitionSeries()
	{
		return maps.stream().map(mr -> mr.transitionSeries).collect(toList());
	}
	
	/**
	 * 
	 * Places a value at a given index for the {@link RawMap} data associated
	 * with the given {@link ITransitionSeries}. This method will apply the
	 * {@link CalibrationProfile} to the added values.
	 * 
	 * @param intensity the intensity value to place in the {@link RawMap} data
	 * @param ts        the {@link ITransitionSeries} associated with the desired
	 *                  {@link RawMap}
	 * @param index     the index in the map data at which to place the new value
	 */
	public void putIntensityInMapAtPoint(float intensity, ITransitionSeries ts, int index)
	{	
		
		
		RawMap m = getMap(ts);
		if (m == null) return;

		m.setData(index, intensity);

	}


	public Iterator<RawMap> iterator()
	{
		return maps.iterator();
	}
	
	
	public Spectrum getSummedRawMap(CalibrationProfile profile) {
		Spectrum s = new ISpectrum(maps.get(0).size());
		for (RawMap map : maps) {
			SpectrumCalculations.addLists_inplace(s, map.getData(profile));
		}
		return s;
	}
	
	public boolean areAllPointsValid() {
		return allPointsValid;
	}
	
	
}
