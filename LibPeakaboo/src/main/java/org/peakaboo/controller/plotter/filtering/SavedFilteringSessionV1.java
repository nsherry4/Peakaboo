package org.peakaboo.controller.plotter.filtering;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.filter.model.SerializedFilterV1;
import org.peakaboo.framework.accent.log.OneLog;

@Deprecated(since = "6", forRemoval = true)
public class SavedFilteringSessionV1 {
	
	
	public List<SerializedFilterV1> filters = new ArrayList<>();

	
	@Deprecated(since = "6", forRemoval = true)
	public void loadInto(FilteringController controller, List<String> errors) {
		FilterSet filterset = controller.getFilteringModel().filters;
		filterset.clear();
		for (SerializedFilterV1 f : this.filters) {
			try {
				filterset.add(f.getFilter(errors).orElseThrow());
			} catch (Exception e) {
				errors.add("Failed to restore filter " + f.getClazz());
				OneLog.log(Level.WARNING, "Failed to restore filter " + f.getClazz(), e);
			}
		}
	}
	
	
	
}
