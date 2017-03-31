package peakaboo.datasource.components.dimensions;

import scitypes.Bounds;
import scitypes.Coord;
import scitypes.SISize;


public interface Dimensions
{

	/**
	 * Get the real (spatial) coordinates for the scan at the given index
	 * @param index the index of the scan to get coordinates for
	 * @return the real (spatial) coordinates for the requested scan
	 */
	Coord<Number> getRealCoordinatesAtIndex(int index) throws IndexOutOfBoundsException;

	/**
	 * Get the real (spatial) dimensions of this map.
	 * @return x,y pair of start,end pairs of spatial dimensions
	 */
	Coord<Bounds<Number>> getRealDimensions();

	/**
	 * Returns a string representation of the units used in measurement
	 * @return the units used for measurement
	 */
	SISize getRealDimensionsUnit();

	/**
	 * Returns the dimensions of the data in this map
	 * @return integer x,y pair of values indicating the width and height of this map
	 */
	Coord<Integer> getDataDimensions();
	
	
	/**
	 * Get the x, y coordinates in the 2D raster map from the index
	 * @param index the index of the scan to get coordinates for
	 * @return the coordinates for the requested scan
	 */
	Coord<Integer> getDataCoordinatesAtIndex(int index) throws IndexOutOfBoundsException;


	
}
