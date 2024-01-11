package org.peakaboo.controller.mapper.fitting.modes;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.fitting.modes.components.VisibilityState;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.framework.cyclops.Coord;
import org.peakaboo.framework.eventful.EventfulBeacon;

public abstract class SimpleModeController extends EventfulBeacon implements ModeController {

	private VisibilityState visibility;
	private MappingController map;
	
	public SimpleModeController(MappingController map) {
		this.map = map;
		this.visibility = new VisibilityState(this);		
	}
	
	public MappingController getMap() {
		return map;
	}

	public Coord<Integer> getSize() {
		int w = getMap().getFiltering().getFilteredDataWidth();
		int h = getMap().getFiltering().getFilteredDataHeight();
		Coord<Integer> size = new Coord<>(w, h);
		return size;
	}


	///// Visibility delegators ///// 
	@Override
	public synchronized List<ITransitionSeries> getAll() { return visibility.getAll(); }
	
	@Override
	public synchronized List<ITransitionSeries> getVisible() { return visibility.getVisible(); }

	@Override
	public synchronized boolean getVisibility(ITransitionSeries ts) { return visibility.getVisibility(ts); }
	
	@Override
	public synchronized void setVisibility(ITransitionSeries ts, boolean visible) {	this.visibility.setVisibility(ts, visible); }

	@Override
	public void setAllVisible(boolean visible) { this.visibility.setAllVisible(visible); }
	
	
	
			
	protected static String getDatasetTitle(List<ITransitionSeries> list) {
		List<String> elementNames = list.stream().map(ITransitionSeries::toString).collect(toList());
		String title = elementNames.stream().collect(joining(", "));
		if (title == null) return "-";
		return title;
	}
	
	
	@Override
	public boolean isSpatial() {
		return true;
	}
	
	@Override
	public boolean isTranslatableToSpatial() {
		return true;
	}
	

	@Override
	public List<Integer> translateSelectionToSpatial(List<Integer> points) {
		return points;
	}

		
	@Override
	public boolean isComparable() {
		return true;
	}
	

	@Override
	public List<Integer> filterSelection(List<Integer> points) {
		return new ArrayList<>(points);
	}
		
}
