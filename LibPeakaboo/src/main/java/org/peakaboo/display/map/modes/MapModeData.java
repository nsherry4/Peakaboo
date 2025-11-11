package org.peakaboo.display.map.modes;

import java.util.Optional;

import org.peakaboo.framework.accent.Coord;
import org.peakaboo.framework.cyclops.spectrum.Spectrum;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public interface MapModeData {

	Coord<Integer> getSize();
	String getValueAtCoord(Coord<Integer> coord);
	
	/**
	 * Reports coordinate information for this map. By convention, a -1 index should
	 * be used for non-spatial maps without a direct mapping to a scan index
	 */
	public static record CoordInfo(int index, int x, int y, String value) {};
	
	default Optional<CoordInfo> getCoordInfo(Coord<Integer> coord) {
		if (isPointInBounds(coord)) {
			int index = getIndex(coord);
			return Optional.of(new CoordInfo(index+1, coord.x+1, coord.y+1, getValueAtCoord(coord)));
		} else {
			return Optional.empty();
		}
		
	}
	
	
	
	/** 
	 * This data is used to giude advanced map selection masking 
	 */
	public static record SelectionInfo(Spectrum map, IntArrayList unselectable) {};
	
	/**
	 * Returns a view of the map used to guide similarity-based selection masking,
	 * along with a list of unselectable points
	 */
	public Optional<SelectionInfo> getMapSelectionInfo();
	
	
	default int getIndex(Coord<Integer> coord) {
		if (isPointInBounds(coord)) {
			return getSize().x * coord.y + coord.x;
		} else {
			return -1;
		}
	}
	
	default boolean isPointInBounds(Coord<Integer> coord) {
		if (coord == null) { return false; }
		return (coord.x >= 0 && coord.x < getSize().x && coord.y >= 0 && coord.y < getSize().y);
	}
	
}
