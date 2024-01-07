package org.peakaboo.controller.plotter.filtering;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.peakaboo.app.PeakabooLog;
import org.peakaboo.filter.model.FilterSet;
import org.peakaboo.filter.model.SerializedFilterV1;

@Deprecated(since = "6", forRemoval = true)
public class SavedFilteringSessionV1 {
	
	
	public List<SerializedFilterV1> filters = new ArrayList<>();

	
	@Deprecated(since = "6", forRemoval = true)
	public void loadInto(FilteringController controller) {
		FilterSet filterset = controller.getFilteringModel().filters;
		filterset.clear();
		for (SerializedFilterV1 f : this.filters) {
			try {
				filterset.add(f.getFilter());
			} catch (Exception e) {
				PeakabooLog.get().log(Level.SEVERE, "Failed to restore filter", e);
			}
		}
	}
	
	
	
}
