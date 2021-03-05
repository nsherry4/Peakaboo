package org.peakaboo.controller.mapper.fitting.modes.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.peakaboo.controller.mapper.fitting.modes.ModeController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;

public class VisibilityState extends AbstractState {

	private Map<ITransitionSeries, Boolean> visibility = new LinkedHashMap<>();
	
	public VisibilityState(ModeController mode) {
		super(mode);
		for (ITransitionSeries ts : mode.getMap().rawDataController.getMapResultSet().getAllTransitionSeries()) {
			visibility.put(ts, true);
		}
	}
	
	/**
	 * Return a copy of the TS keys, sorted
	 */
	public synchronized List<ITransitionSeries> getAll() {
		List<ITransitionSeries> tsList = new ArrayList<>(visibility.keySet());
		Collections.sort(tsList);
		return tsList;
	}
	
	public synchronized List<ITransitionSeries> getVisible() {
		List<ITransitionSeries> visible = new ArrayList<>();
		for (ITransitionSeries ts : getAll()) {
			if (getVisibility(ts)) {
				visible.add(ts);
			}
		}
		return visible;
	}
	
	/**
	 * Returns if this TransitionSeries is enabled, but returning false regardless
	 * of setting if {@link #getTransitionSeriesEnabled(ITransitionSeries)} returns
	 * false
	 */
	public synchronized boolean getVisibility(ITransitionSeries ts) {
		return this.visibility.get(ts) && mode.getMap().getFitting().getTransitionSeriesEnabled(ts);
	}
	
	public synchronized void setVisibility(ITransitionSeries ts, boolean visible) {
		this.visibility.put(ts, visible);
		mode.updateListeners();
	}
	
	public void setAllVisible(boolean visible) {
		for (ITransitionSeries ts : getAll()) {
			this.visibility.put(ts, visible);
		}
		mode.updateListeners();
	}
	
}
