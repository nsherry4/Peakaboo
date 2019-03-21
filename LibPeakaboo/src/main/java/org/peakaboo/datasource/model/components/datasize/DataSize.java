package org.peakaboo.datasource.model.components.datasize;

import org.peakaboo.framework.cyclops.Coord;


public interface DataSize
{


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
