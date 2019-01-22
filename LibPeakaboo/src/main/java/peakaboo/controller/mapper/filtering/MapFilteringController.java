package peakaboo.controller.mapper.filtering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cyclops.Coord;
import cyclops.ReadOnlySpectrum;
import eventful.EventfulCache;
import eventful.EventfulType;
import peakaboo.calibration.CalibrationProfile;
import peakaboo.controller.mapper.MappingController;
import peakaboo.controller.mapper.MappingController.UpdateType;
import peakaboo.curvefit.peak.transition.ITransitionSeries;
import peakaboo.mapping.filter.model.AreaMap;
import peakaboo.mapping.filter.model.MapFilter;
import peakaboo.mapping.filter.model.MapFilterSet;
import peakaboo.mapping.rawmap.RawMap;
import peakaboo.mapping.rawmap.RawMapSet;

public class MapFilteringController extends EventfulType<String> {

	private MappingController controller;
	
	private MapFilterSet filters = new MapFilterSet();
	private EventfulCache<Map<ITransitionSeries, AreaMap>> cachedMaps;

	
	public MapFilteringController(MappingController controller) {
		this.controller = controller;
		cachedMaps = new EventfulCache<>(this::filterMaps);
	}
	
	
	private Map<ITransitionSeries, AreaMap> filterMaps() {
		
		Map<ITransitionSeries, AreaMap> areamaps = new HashMap<>();
		
		Coord<Integer> size = controller.getSettings().getView().viewDimensions;

		//get calibrated map data and generate AreaMaps
		//TODO: Move this to a CalibrationController which can cache the calibrated data?
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
	
	public AreaMap getAreaMap(ITransitionSeries ts) {
		return cachedMaps.getValue().get(ts);
	}
	
	public List<AreaMap> getAreaMaps(List<ITransitionSeries> tss) {
		return tss.stream().map(this::getAreaMap).collect(Collectors.toList());
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
	
	

	
	
	
}
