package org.peakaboo.controller.mapper.fitting.modes.components;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.peakaboo.controller.mapper.fitting.modes.ModeController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;

public class GroupState extends AbstractState {

	private Map<ITransitionSeries, Integer> groupings = new LinkedHashMap<>();
	
	public GroupState(ModeController mode) {
		super(mode);
		setAll(1);
	}
	
	public void setAll(Integer group) {
		for (ITransitionSeries ts : mode.getMap().rawDataController.getMapResultSet().getAllTransitionSeries()) {
			this.groupings.put(ts, group);
		}
		mode.updateListeners();
	}
	
	public void setGroup(ITransitionSeries ts, Integer group) {
		this.groupings.put(ts, group);
		mode.updateListeners();
	}
	
	public int getGroup(ITransitionSeries ts) {
		return groupings.get(ts);
	}
	
	public List<ITransitionSeries> getMembers(final int group) {
		List<ITransitionSeries> tss = new ArrayList<>();
		for (ITransitionSeries ts : groupings.keySet()) {
			if (getGroup(ts) == group) {
				tss.add(ts);
			}
		}
		return tss;
	}
	
	public List<ITransitionSeries> getVisibleMembers(final int group) {
		List<ITransitionSeries> tss = new ArrayList<>();
		for (ITransitionSeries ts : groupings.keySet()) {
			if (getGroup(ts) == group && mode.getVisibility(ts)) {
				tss.add(ts);
			}
		}
		return tss;
	}
	
}
