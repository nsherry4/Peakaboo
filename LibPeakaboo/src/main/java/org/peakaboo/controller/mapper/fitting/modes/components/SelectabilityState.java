package org.peakaboo.controller.mapper.fitting.modes.components;

import org.peakaboo.controller.mapper.fitting.modes.ModeController;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;

import it.unimi.dsi.fastutil.ints.IntArrayList;

/**
 * Manages state for map modes that want some points to be unselectable. This state component never emits mode update messages. 
 */
public class SelectabilityState extends AbstractState {

	private boolean[] selectable;
	private GridPerspective<Boolean> grid;
	
	public SelectabilityState(ModeController mode) {
		super(mode);
		clear();
	}
	
	public void clear() {
		Coord<Integer> size = mode.getSize();
		selectable = new boolean[size.x*size.y];
		for (int i = 0; i < selectable.length; i++) {
			selectable[i] = true;
		}
		grid = new GridPerspective<Boolean>(size.x, size.y, false);
	}
	
	public boolean get(int index) {
		return selectable[index];
	}
	public boolean get(int x, int y) {
		return get(grid.getIndexFromXY(x, y));
	}

	public IntArrayList selectables() {
		IntArrayList indices = new IntArrayList();
		for (int i = 0; i < selectable.length; i++) {
			if (selectable[i]) { indices.add(i); }
		}
		return indices;
	}
	public IntArrayList unselectables() {
		IntArrayList indices = new IntArrayList();
		for (int i = 0; i < selectable.length; i++) {
			if (!selectable[i]) { indices.add(i); }
		}
		return indices;
	}
	
	public void set(int index, boolean indexSelectable) {
		selectable[index] = indexSelectable;
	}
	public void set(int x, int y, boolean indexSelectable) {
		set(grid.getIndexFromXY(x, y), indexSelectable);
	}
	
	/**
	 * Filter a list of indexes so that only those which are selectable are returned
	 */
	public IntArrayList filter(IntArrayList indexList) {
		IntArrayList filtered = new IntArrayList();
		for (int index : indexList) {
			if (get(index)) { filtered.add(index); }
		}
		return filtered;
	}
	
}
