package org.peakaboo.display.map.modes;

import java.util.Optional;

import org.peakaboo.framework.cyclops.Coord;

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
