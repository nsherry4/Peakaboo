package org.peakaboo.controller.mapper.filtering;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.mapping.filter.model.MapFilter;
import org.peakaboo.mapping.filter.model.SerializedMapFilter;

public class SavedMapFilteringSession {

	List<SerializedMapFilter> filters;
	
	
	public SavedMapFilteringSession storeFrom(MapFilteringController controller) {
		filters = new ArrayList<>();
		for (MapFilter filter : controller.getAll()) {
			this.filters.add(new SerializedMapFilter(filter));
		}
		return this;
	}
	
	public void loadInto(MapFilteringController controller) {
		controller.clear();
		for (SerializedMapFilter f : this.filters) {
			controller.add(f.getFilter());
		}
	}
	
}
