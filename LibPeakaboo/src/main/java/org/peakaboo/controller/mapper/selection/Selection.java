package org.peakaboo.controller.mapper.selection;

import java.util.List;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.framework.eventful.IEventfulType;

public interface Selection extends IEventfulType<MapUpdateType> {

	/**
	 * generate a list of indexes in the map which are selected
	 */
	List<Integer> getPoints();

	boolean hasSelection();

	boolean isReplottable();

	void clearSelection();

}