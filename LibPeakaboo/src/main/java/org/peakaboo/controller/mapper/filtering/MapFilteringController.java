package org.peakaboo.controller.mapper.filtering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.MappingController.UpdateType;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.model.MapFilter;
import org.peakaboo.mapping.filter.model.MapFilterSet;
import org.peakaboo.mapping.rawmap.RawMap;
import org.peakaboo.mapping.rawmap.RawMapSet;

import cyclops.Coord;
import cyclops.ReadOnlySpectrum;
import cyclops.util.ListOps;
import eventful.EventfulCache;
import eventful.EventfulType;

public class MapFilteringController extends EventfulType<String> {

	private MappingController controller;
	
	private MapFilterSet filters = new MapFilterSet();
	private EventfulCache<Map<ITransitionSeries, AreaMap>> cachedMaps;
	private EventfulCache<AreaMap> summedMap;
	
	
	public MapFilteringController(MappingController controller) {
		this.controller = controller;
		cachedMaps = new EventfulCache<>(this::filterMaps);
		summedMap = new EventfulCache<>(this::sumMaps);
		summedMap.addUpstreamDependency(cachedMaps);
	}
	
	
	private Map<ITransitionSeries, AreaMap> filterMaps() {
		
		Map<ITransitionSeries, AreaMap> areamaps = new HashMap<>();
		
		Coord<Integer> size = controller.getSettings().getView().viewDimensions;

		//get calibrated map data and generate AreaMaps
		CalibrationProfile profile = controller.rawDataController.getCalibrationProfile();
		
		RawMapSet rawmaps = controller.rawDataController.getMapResultSet();
		for (RawMap rawmap : rawmaps) {
			ITransitionSeries ts = rawmap.transitionSeries;
			ReadOnlySpectrum calibrated = rawmaps.getMap(ts).getData(profile);
			AreaMap areamap = new AreaMap(calibrated, size);
			areamap = apply(areamap);
			areamaps.put(ts, areamap);
		}

		return areamaps;
		
	}
	
	private AreaMap sumMaps() {
		return AreaMap.sum(new ArrayList<>(cachedMaps.getValue().values()));
	}
	
	public AreaMap getAreaMap(ITransitionSeries ts) {
		return cachedMaps.getValue().get(ts);
	}
	
	public List<AreaMap> getAreaMaps(List<ITransitionSeries> tss) {
		return tss.stream().map(this::getAreaMap).collect(Collectors.toList());
	}
	
	/**
	 * Returns the sum of all maps, or null, if there are no maps.
	 */
	public AreaMap getSummedMap() {
		return summedMap.getValue();
	}
	
	private AreaMap apply(AreaMap map) {
		return filters.apply(map);
	}

	public boolean add(MapFilter e) {
		boolean result = filters.add(e);
		filteredDataInvalidated();
		return result;
	}

	public boolean remove(MapFilter o) {
		boolean result = filters.remove(o);
		filteredDataInvalidated();
		return result;
	}

	public void moveMapFilter(int from, int to) {
		//we'll be removing the item from the list, so if the 
		//destination is greater than the source, decrement it 
		//to make up the difference
		if (to > from) { to--; }
		
		MapFilter filter = filters.get(from);
		filters.remove(from);
		filters.add(to, filter);
		filteredDataInvalidated();
	}
	
	public void clear() {
		filters.clear();
		filteredDataInvalidated();
	}

	public MapFilter get(int index) {
		return filters.get(index);
	}

	public MapFilter remove(int index) {
		MapFilter filter = filters.remove(index);
		filteredDataInvalidated();
		return filter;
	}

	public boolean contains(MapFilter o) {
		return filters.contains(o);
	}

	public List<MapFilter> getAll() {
		return filters.getAll();
	}

	public int size() {
		return filters.size();
	}

	public void setMapFilterEnabled(int index, boolean enabled) {
		filters.get(index).setEnabled(enabled);
		filteredDataInvalidated();
	}
	
	public void filteredDataInvalidated() {
		cachedMaps.invalidate();
		updateListeners(UpdateType.FILTER.toString());
	}
	
	
	public String getActionDescription() {
		List<String> actions = filters.getAll().stream().map(f -> f.getFilterAction()).collect(Collectors.toList());
		return ListOps.unique(actions).stream().reduce((a, b) -> a + ", " + b).orElse(null);
	}
	

	
}
