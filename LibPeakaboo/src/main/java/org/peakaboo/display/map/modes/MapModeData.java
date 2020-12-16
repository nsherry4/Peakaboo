package org.peakaboo.display.map.modes;

import org.peakaboo.framework.cyclops.Coord;

public interface MapModeData {

	Coord<Integer> getSize();
	String getValueAtCoord(Coord<Integer> coord);
	
	default String getInfoAtCoord(Coord<Integer> coord) {
		String noValue = "Index: -, X: -, Y: -, Value: -";
		if (isPointInBounds(coord)) {
			int index = getIndex(coord);
			return "Index: " + (index+1) + ", X: " + (coord.x+1) + ", Y: " + (coord.y+1) + ", Value: " + getValueAtCoord(coord);
		} else {
			return noValue;
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
