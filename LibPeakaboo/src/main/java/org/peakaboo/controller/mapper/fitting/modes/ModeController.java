package org.peakaboo.controller.mapper.fitting.modes;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.display.map.modes.MapModeData;
import org.peakaboo.framework.cyclops.ISpectrum;
import org.peakaboo.framework.cyclops.Spectrum;
import org.peakaboo.framework.eventful.Eventful;
import org.peakaboo.mapping.filter.model.AreaMap;

public abstract class ModeController extends Eventful {

	private Map<ITransitionSeries, Boolean> visibility = new LinkedHashMap<>();
	private MappingController map;
	
	public ModeController(MappingController map) {
		this.map = map;
		
		for (ITransitionSeries ts : map.rawDataController.getMapResultSet().getAllTransitionSeries()) {
			visibility.put(ts, true);
		}
		
	}
	
	

	protected MappingController getMap() {
		return map;
	}



	public synchronized List<ITransitionSeries> getAll()
	{
		//TODO: This shouldn't be done this way any more
		List<ITransitionSeries> tsList = this.visibility.keySet().stream().filter(a -> true).collect(toList());
		Collections.sort(tsList);
		return tsList;
	}
	

	public synchronized List<ITransitionSeries> getVisible()
	{
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
	public synchronized boolean getVisibility(ITransitionSeries ts)
	{
		return this.visibility.get(ts) && map.getFitting().getTransitionSeriesEnabled(ts);
	}
	public synchronized void setVisibility(ITransitionSeries ts, boolean visible)
	{
		this.visibility.put(ts, visible);
		updateListeners();
	}

	
	public void setAllVisible(boolean visible) {
		for (ITransitionSeries ts : getAll()) {
			setVisibility(ts, visible);
		}
	}
	
	
	
	protected Spectrum sumVisibleMaps()
	{	
		return sumGivenMaps(getVisible());
	}
	
	
	protected Spectrum sumGivenMaps(List<ITransitionSeries> list)
	{
		int y = map.getFiltering().getFilteredDataHeight();
		int x = map.getFiltering().getFilteredDataWidth();
		
		
		//filter the maps
		Iterable<AreaMap> filtereds = map.getFiltering().getAreaMaps(list);
		
		//merge the maps into a single composite map
		if (!filtereds.iterator().hasNext()) {
			//TODO: This may give a technically wrong result until interpolation is made into a filter
			return new ISpectrum(x * y);
		}
		
		return AreaMap.sumSpectrum(filtereds);
		
	}
	
	
	protected static String getDatasetTitle(List<ITransitionSeries> list)
	{
		
		List<String> elementNames = list.stream().map(ts -> ts.toString()).collect(toList());
		String title = elementNames.stream().collect(joining(", "));
		if (title == null) return "-";
		return title;
		
	}

	protected Spectrum sumSingleMap(ITransitionSeries ts)
	{
		return sumGivenMaps(Collections.singletonList(ts));
	}
	
	
	public abstract String longTitle();
	public abstract MapModeData getData();
	public abstract boolean isSelectable();
	public abstract boolean isReplottable();

	
}
