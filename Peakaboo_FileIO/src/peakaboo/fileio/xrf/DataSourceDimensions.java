package peakaboo.fileio.xrf;

import peakaboo.datatypes.Coord;
import peakaboo.datatypes.Range;


public interface DataSourceDimensions
{

	/**
	 * Get the real (spatial) coordinates for the scan at the given index
	 * @param index the index of the scan to get coordinates for
	 * @return the real (spatial) coordinates for the requested scan
	 */
	public Coord<Number> getRealCoordinatesAtIndex(int index);

	/**
	 * Get the real (spatial) dimensions of this map.
	 * @return x,y pair of start,end pairs of spatial dimensions
	 */
	public Coord<Range<Number>> getRealDimensions();

	/**
	 * Returns a string representation of the units used in measurement
	 * @return the units used for measurement
	 */
	public String getRealDimensionsUnit();

	/**
	 * Returns the dimensions of the data in this map
	 * @return integer x,y pair of values indicating the width and height of this map
	 */
	public Coord<Integer> getDataDimensions();
	
}
