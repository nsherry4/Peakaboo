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
		// A null means unset -- we must set the value lazily because the
		// MapFittingController isn't ready here
		for (ITransitionSeries ts : mode.getMap().rawDataController.getMapResultSet().getAllTransitionSeries()) {
			visibility.put(ts, null);
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
	 * Returns if this TransitionSeries is visible. Until the user sets it, only
	 * nominal TransitionSeries are visible.
	 */
	public synchronized boolean getVisibility(ITransitionSeries ts) {
		Boolean visible = this.visibility.get(ts);
		if (visible == null) {
			visible = mode.getMap().getFitting().isTransitionSeriesNominal(ts);
		}
		return visible;
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
