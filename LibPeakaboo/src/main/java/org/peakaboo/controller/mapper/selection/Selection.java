package org.peakaboo.controller.mapper.selection;

import java.util.Optional;

import org.peakaboo.framework.autodialog.model.Group;
import org.peakaboo.framework.accent.Coord;

import it.unimi.dsi.fastutil.ints.IntArrayList;

public interface Selection {

	Optional<Group> getParameters();

	IntArrayList selectPoint(Coord<Integer> clickedAt, boolean singleSelect);

	IntArrayList startDragSelection(Coord<Integer> point);
	IntArrayList addDragSelection(Coord<Integer> point);
	IntArrayList releaseDragSelection(Coord<Integer> point);

	/**
	 * Reset the selection's internal state, clearing any in-progress work.
	 * Default implementation is no-op for stateless selections.
	 */
	default void reset() {
		// Default no-op - selections without internal state don't need to override
	}

	/**
	 * Check if this selection is complete and ready to commit.
	 * Most selections complete immediately (return true).
	 * Incremental selections like polygon return false until closed.
	 *
	 * @return true if selection is complete, false if still building
	 */
	default boolean isClosed() {
		return true;
	}

}