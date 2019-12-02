package org.peakaboo.mapping.filter.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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
	
	public synchronized boolean isReplottable() {
		boolean replottable = true;
		for (MapFilter f : filters) {
			if (!f.isEnabled()) {
				continue;
			}
			replottable &= f.isReplottable();
		}
		return replottable;
	}
	
	@Override
	public Iterator<MapFilter> iterator() {
		return filters.iterator();
	}
	
	public synchronized List<MapFilter> getAll() {
		return new ArrayList<>(filters);
	}
	
	public List<MapFilter> getAllEnabled() {
		return filters.stream().filter(MapFilter::isEnabled).collect(Collectors.toList());
	}
	
	public AreaMap apply(AreaMap map) {
		
		for (MapFilter filter : filters) {
			if (filter.isEnabled()) {
				map = filter.filter(map);
			}
		}
		
		return map;
	}



}
