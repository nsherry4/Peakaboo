package org.peakaboo.controller.mapper.filtering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.peakaboo.calibration.CalibrationProfile;
import org.peakaboo.controller.mapper.MappingController;
import org.peakaboo.controller.mapper.MappingController.UpdateType;
import org.peakaboo.curvefit.peak.transition.ITransitionSeries;
import org.peakaboo.mapping.filter.model.AreaMap;
import org.peakaboo.mapping.filter.model.MapFilter;
import org.peakaboo.mapping.filter.model.MapFilterSet;
import org.peakaboo.mapping.rawmap.RawMapSet;

import cyclops.Bounds;
import cyclops.Coord;
import cyclops.ISpectrum;
import cyclops.ReadOnlySpectrum;
import cyclops.util.ListOps;
import eventful.EventfulCache;
import eventful.EventfulType;

public class MapFilteringController extends EventfulType<String> {

	private MappingController controller;
	
	private MapFilterSet filters = new MapFilterSet();
	private EventfulCache<CachedMaps> cachedMaps;
	
	
	public MapFilteringController(MappingController controller) {
		this.controller = controller;
		cachedMaps = new EventfulCache<>(() -> new CachedMaps(controller, filters));
		
		controller.addListener(t -> {
			if (UpdateType.DATA.toString().equals(t) || UpdateType.DATA_SIZE.toString().equals(t)) {
				filteredDataInvalidated();
			}
		});
		
	}
	
	
	public int getFilteredDataWidth() {
		return getSummedMap().getSize().x;
	}
		
	public int getFilteredDataHeight() {
		return getSummedMap().getSize().y;
	}
	
	public boolean isValidPoint(Coord<Integer> mapCoord)
	{
		return (mapCoord.x >= 0 && mapCoord.x < getFilteredDataWidth() && mapCoord.y >= 0 && mapCoord.y < getFilteredDataHeight());
	}
	
	
	/**
	 * Returns true if and only if the size of the filtered maps does not match the
	 * user specified size.
	 */
	private boolean filteringChangedMapSize() {
		return 
				controller.getUserDimensions().getUserDataWidth() != getFilteredDataWidth()
				||
				controller.getUserDimensions().getUserDataHeight() != getFilteredDataHeight();
	}
	
	/**
	 * Indicates that the map's pixels still line up with the input spectra
	 * @return true if the map pixels still line up with the input spectra, false otherwise
	 */
	public boolean isReplottable() {
		return (!filteringChangedMapSize()) && (this.cachedMaps.getValue().replottable);
	}
	
	
	public AreaMap getAreaMap(ITransitionSeries ts) {
		return cachedMaps.getValue().maps.get(ts);
	}
	
	public List<AreaMap> getAreaMaps(List<ITransitionSeries> tss) {
		return tss.stream().map(this::getAreaMap).collect(Collectors.toList());
	}
	
	/**
	 * Returns the sum of all maps, or null, if there are no maps.
	 */
	public AreaMap getSummedMap() {
		return cachedMaps.getValue().sum;
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
		List<String> actions = filters.getAll().stream().map(f -> f.getFilterDescriptor().getAction()).collect(Collectors.toList());
		return ListOps.unique(actions).stream().reduce((a, b) -> a + ", " + b).orElse(null);
	}


	/**
	 * Returns true if the map data is being changed by the filters
	 */
	public boolean isFiltering() {
		return filters.getAll().stream().map(f -> f.isEnabled()).reduce(false, (a, b) -> a || b);
	}
	
	public Coord<Bounds<Number>> getRealDimensions() {
		if (cachedMaps.getValue().sum == null) {
			return null;
		}
		return cachedMaps.getValue().sum.getRealDimensions();
	}
	

	
}

class CachedMaps {
	
	public Map<ITransitionSeries, AreaMap> maps;
	public AreaMap sum;
	public boolean replottable;
	
	public CachedMaps(MappingController controller, MapFilterSet filters) {

		maps = new ConcurrentHashMap<>();
		
		Coord<Integer> size = controller.getUserDimensions().getDimensions();

		//get calibrated map data and generate AreaMaps
		CalibrationProfile profile = controller.rawDataController.getCalibrationProfile();
		
		RawMapSet rawmaps = controller.rawDataController.getMapResultSet();
		rawmaps.stream().parallel().forEach(rawmap -> {
			ITransitionSeries ts = rawmap.transitionSeries;
			ReadOnlySpectrum calibrated = rawmaps.getMap(ts).getData(profile);
			AreaMap areamap = new AreaMap(calibrated, size, controller.rawDataController.getRealDimensions());
			areamap = filters.applyUnsynchronized(areamap);
			maps.put(ts, areamap);
		});

		this.replottable = filters.isReplottable();
		
		if (maps.size() > 0) {
			sum = AreaMap.sum(new ArrayList<>(maps.values()));
		} else {
			sum = new AreaMap(new ISpectrum(size.x * size.y), size, null);
			sum = filters.applyUnsynchronized(sum);
		}
		
	}
	
	
	
	
}
