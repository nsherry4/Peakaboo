package org.peakaboo.controller.mapper.fitting.modes.components;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.controller.mapper.fitting.modes.ModeController;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.cyclops.GridPerspective;

/**
 * Manages state for map modes that want some points to be unselectable
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
		mode.updateListeners();
	}
	
	public boolean get(int index) {
		return selectable[index];
	}
	public boolean get(int x, int y) {
		return get(grid.getIndexFromXY(x, y));
	}

	public List<Integer> selectables() {
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < selectable.length; i++) {
			if (selectable[i]) { indices.add(i); }
		}
		return indices;
	}
	public List<Integer> unselectables() {
		List<Integer> indices = new ArrayList<>();
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
	public List<Integer> filter(List<Integer> indexList) {
		List<Integer> filtered = new ArrayList<>();
		for (int index : indexList) {
			if (get(index)) { filtered.add(index); }
		}
		return filtered;
	}
	
}
