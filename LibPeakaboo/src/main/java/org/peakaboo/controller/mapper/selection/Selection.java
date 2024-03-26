package org.peakaboo.controller.mapper.selection;

import java.util.Optional;

import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.cyclops.Coord;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public interface Selection {
	
	Optional<Group> getParameters();
	
	IntArrayList selectPoint(Coord<Integer> clickedAt, boolean singleSelect);
	
	IntArrayList startDragSelection(Coord<Integer> point);
	IntArrayList addDragSelection(Coord<Integer> point);
	IntArrayList releaseDragSelection(Coord<Integer> point);

}