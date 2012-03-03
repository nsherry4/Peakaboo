package peakaboo.datasource.interfaces;

import scitypes.Bounds;
import scitypes.Coord;


public interface DSRealDimensions
{

	/**
	 * Get the real (spatial) coordinates for the scan at the given index
	 * @param index the index of the scan to get coordinates for
	 * @return the real (spatial) coordinates for the requested scan
	 */
	public Coord<Number> getRealCoordinatesAtIndex(int index) throws IndexOutOfBoundsException;

	/**
	 * Get the real (spatial) dimensions of this map.
	 * @return x,y pair of start,end pairs of spatial dimensions
	 */
	public Coord<Bounds<Number>> getRealDimensions();

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
	
	
	/**
	 * Get the x, y coordinates in the 2D raster map from the index
	 * @param index the index of the scan to get coordinates for
	 * @return the coordinates for the requested scan
	 */
	public Coord<Integer> getDataCoordinatesAtIndex(int index) throws IndexOutOfBoundsException;


	
}
