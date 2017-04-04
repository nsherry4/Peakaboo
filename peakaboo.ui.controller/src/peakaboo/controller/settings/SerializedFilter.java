package peakaboo.controller.settings;

import java.util.Map;

import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilterSet;

public class SerializedFilter {


	private Filter filter;
	
	//These values exist only to initialize the filter, not to be read from.
	private String clazz;
	private Map<Integer, Object> settings;
	
	
	
	public SerializedFilter() {	}
	
	public SerializedFilter(Filter filter) {
		
		this.filter = filter;
		
	}



	public String getClazz() {
		return filter.getClass().getName();
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public Map<Integer, Object> getSettings() {
		System.out.println(filter.save());
		return filter.save();
	}

	public void setSettings(Map<Integer, Object> settings) {
		this.settings = settings;
	}

	public Filter getFilter() {
		if (filter != null) { return filter; }
			
		FilterSet filterset = new FilterSet();
		for (Filter f : filterset.getAvailableFilters()) {
			if (f.getClass().getName().equals(clazz)) {
				filter = f;
				filter.initialize();
				System.out.println(settings);
				filter.load(settings);
				return filter;
			}
		}
		throw new RuntimeException("Cannot find plugin " + clazz);
	}

	
	
	
	
	
	
	
}
