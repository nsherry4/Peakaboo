package org.peakaboo.dataset.source.model.components.datasize;

import org.peakaboo.framework.accent.Coord;


public interface DataSize
{


	/**
	 * Returns the dimensions of the data in this map
	 * @return integer x,y pair of values indicating the width and height of this map
	 */
	Coord<Integer> getDataDimensions();
	
	/**
	 * Returns the size in terms of the total number of data points (ie x*y) 
	 */
	default int size() {
		Coord<Integer> dims = getDataDimensions();
		return dims.x * dims.y;
	}
	
	/**
	 * Get the x, y coordinates in the 2D raster map from the index
	 * @param index the index of the scan to get coordinates for
	 * @return the coordinates for the requested scan
	 */
	Coord<Integer> getDataCoordinatesAtIndex(int index) throws IndexOutOfBoundsException;


	
}
