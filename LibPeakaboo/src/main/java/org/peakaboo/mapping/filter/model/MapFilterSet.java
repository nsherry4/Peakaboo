package org.peakaboo.mapping.filter.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapFilterSet implements Iterable<MapFilter> {

	private List<MapFilter> filters;

	public MapFilterSet() {
		filters = new ArrayList<>();
	}

	public synchronized boolean add(MapFilter e) {
		return filters.add(e);
	}

	public synchronized boolean remove(MapFilter o) {
		return filters.remove(o);
	}

	public synchronized void clear() {
		filters.clear();
	}

	public synchronized MapFilter get(int index) {
		return filters.get(index);
	}

	public synchronized void add(int index, MapFilter element) {
		filters.add(index, element);
	}

	public synchronized MapFilter remove(int index) {
		return filters.remove(index);
	}

	public synchronized int size() {
		return filters.size();
	}

	public synchronized boolean contains(MapFilter o) {
		return filters.contains(o);
	}

	public synchronized int indexOf(MapFilter o) {
		return filters.indexOf(o);
	}

	public synchronized void moveMapFilterUp(int index) {
		MapFilter filter = get(index);
		index -= 1;
		if(index < 0) index = 0;
		remove(filter);
		add(index, filter);
	}

	public synchronized void moveMapFilterDown(int index) {
		MapFilter filter = get(index);
		index -= 1;
		if(index >= size()) index = size()-1;
		remove(filter);
		add(index, filter);
	}
	
	@Override
	public Iterator<MapFilter> iterator() {
		return filters.iterator();
	}
	
	public synchronized List<MapFilter> getAll() {
		return new ArrayList<>(filters);
	}
	
	public synchronized AreaMap apply(AreaMap map) {
		
		for (MapFilter filter : filters) {
			if (filter.isEnabled()) {
				map = filter.filter(map);
			}
		}
		
		return map;
	}

}
