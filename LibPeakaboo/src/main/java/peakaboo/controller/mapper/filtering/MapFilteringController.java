package peakaboo.controller.mapper.filtering;

import java.util.List;

import eventful.EventfulType;
import peakaboo.controller.mapper.MappingController.UpdateType;
import peakaboo.mapping.filter.model.AreaMap;
import peakaboo.mapping.filter.model.MapFilter;
import peakaboo.mapping.filter.model.MapFilterSet;

public class MapFilteringController extends EventfulType<String> {

	private MapFilterSet filters = new MapFilterSet();

	public AreaMap apply(AreaMap map) {
		return filters.apply(map);
	}

	public boolean add(MapFilter e) {
		boolean result = filters.add(e);
		updateListeners(UpdateType.FILTER.toString());
		return result;
	}

	public boolean remove(MapFilter o) {
		boolean result = filters.remove(o);
		updateListeners(UpdateType.FILTER.toString());
		return result;
	}

	public void clear() {
		filters.clear();
		updateListeners(UpdateType.FILTER.toString());
	}

	public MapFilter get(int index) {
		return filters.get(index);
	}

	public MapFilter remove(int index) {
		MapFilter filter = filters.remove(index);
		updateListeners(UpdateType.FILTER.toString());
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

	public void filteredDataInvalidated() {
		//TODO: We will eventually cache data in here
		updateListeners(UpdateType.FILTER.toString());
	}

	
	
	
}
