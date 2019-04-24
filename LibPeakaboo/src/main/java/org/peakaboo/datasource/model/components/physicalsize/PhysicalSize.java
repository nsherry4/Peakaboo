package org.peakaboo.datasource.model.components.physicalsize;

import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.SISize;

public interface PhysicalSize {

	/**
	 * Get the real (spatial) coordinates for the scan at the given index
	 * @param index the index of the scan to get coordinates for
	 * @return the real (spatial) coordinates for the requested scan
	 */
	Coord<Number> getPhysicalCoordinatesAtIndex(int index) throws IndexOutOfBoundsException;

	/**
	 * Get the real (spatial) dimensions of this map.
	 * @return x,y pair of start,end pairs of spatial dimensions
	 */
	Coord<Bounds<Number>> getPhysicalDimensions();

	/**
	 * Returns a string representation of the units used in measurement
	 * @return the units used for measurement
	 */
	SISize getPhysicalUnit();

	
}
