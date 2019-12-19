package org.peakaboo.controller.mapper.selection;

import java.util.List;
import java.util.Optional;

import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.cyclops.Coord;

public interface Selection {
	
	Optional<Group> getParameters();
	
	List<Integer> selectPoint(Coord<Integer> clickedAt, boolean singleSelect);
	
	List<Integer> startDragSelection(Coord<Integer> point);
	List<Integer> addDragSelection(Coord<Integer> point);
	List<Integer> releaseDragSelection(Coord<Integer> point);

}