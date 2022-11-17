package org.peakaboo.controller.plotter.filtering;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.filter.model.Filter;
import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.filter.model.SerializedFilter;

public class SavedFilteringSession {
	
	
	public List<SerializedFilter> filters = new ArrayList<>();
	
	
	public SavedFilteringSession storeFrom(FilteringController controller) {
		for (Filter filter : controller.filteringModel.filters) {
			this.filters.add(new SerializedFilter(filter));
		}
		return this;
	}
	
	public void loadInto(FilteringController controller) {
		FilterSet filterset = controller.getFilteringModel().filters;
		filterset.clear();
		for (SerializedFilter f : this.filters) {
			try {
				filterset.add(f.getFilter());
			} catch (Exception e) {
				PeakabooLog.get().log(Level.SEVERE, "Failed to restore filter", e);
			}
		}
	}
	
	
	
}
