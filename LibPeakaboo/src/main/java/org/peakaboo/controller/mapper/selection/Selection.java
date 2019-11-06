package org.peakaboo.controller.mapper.selection;

import java.util.List;
import java.util.Optional;

import org.peakaboo.controller.mapper.MapUpdateType;
import org.peakaboo.datasource.model.internal.SubsetDataSource;
import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.eventful.IEventfulType;

public interface Selection extends IEventfulType<MapUpdateType> {

	/**
	 * generate a list of indexes in the map which are selected
	 */
	List<Integer> getPoints();

	boolean hasSelection();

	void clearSelection();
	
	Optional<Group> getParameters();
	
	SubsetDataSource getSubsetDataSource();
	
	
	void selectPoint(Coord<Integer> clickedAt, boolean singleSelect, boolean modify);
	
	void startDragSelection(Coord<Integer> point);
	void addDragSelection(Coord<Integer> point);
	void releaseDragSelection(Coord<Integer> point);

}