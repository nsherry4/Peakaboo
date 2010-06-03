package peakaboo.mapping;


import java.util.List;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.peaktable.TransitionSeries;

/**
 * 
 * This class stores the data associated with a single map of a scan. Each map is associated with a single
 * {@link TransitionSeries}, and can be marked as visible or invisible. When generating or displaying data for
 * more than one TransitionSeries, it may be desirable to use a {@link MapResultSet} instead of managing the
 * MapResult objects manually.
 * 
 * @author Nathaniel Sherry, 2009
 * 
 */

public class MapResult implements Cloneable
{

	public List<Double>		data;
	public TransitionSeries	transitionSeries;


	public MapResult(TransitionSeries ts, int mapSize)
	{
		this.data = DataTypeFactory.<Double> list(mapSize);
		for (int i = 0; i < mapSize; i++) {
			data.add(0.0);
		}
		transitionSeries = ts;
	}
	
	protected MapResult(TransitionSeries ts, List<Double> data)
	{
		this.transitionSeries = ts;
		this.data = data;
	}

	public MapResult clone()
	{
		return new MapResult(transitionSeries, data);
	}
	

	@Override
	public String toString()
	{
		return transitionSeries.element.name() + " (" + transitionSeries.type + ")";
	}

}
