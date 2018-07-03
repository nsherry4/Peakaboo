package peakaboo.controller.plotter.filtering;

import java.util.ArrayList;
import java.util.List;

import peakaboo.controller.settings.SavedSession;
import peakaboo.controller.settings.SettingsSerializer;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilterSet;
import peakaboo.filter.model.SerializedFilter;

public class SavedFilteringSession {
	
	
	public List<SerializedFilter> filters = new ArrayList<>();
	
	
	/**
	 * Decodes a serialized data object from yaml
	 */
	public static SavedSession deserialize(String yaml) {
		return SettingsSerializer.deserialize(yaml);
	}
	/**
	 * Encodes the serialized data as yaml
	 */
	public String serialize() {
		return SettingsSerializer.serialize(this);
	}
	
	
	
	public static SavedFilteringSession storeFrom(FilteringController controller) {
		SavedFilteringSession saved = new SavedFilteringSession();
		for (Filter filter : controller.filteringModel.filters) {
			saved.filters.add(new SerializedFilter(filter));
		}
		return saved;
	}
	
	public void loadInto(FilteringController controller) {
		FilterSet filterset = controller.getFilteringModel().filters;
		filterset.clear();
		for (SerializedFilter f : this.filters) {
			filterset.add(f.getFilter());
		}
	}
	
	
	
}
