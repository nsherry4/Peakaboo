package peakaboo.controller.settings;

import java.util.HashMap;
import java.util.Map;

import autodialog.model.Parameter;
import peakaboo.filter.model.Filter;
import peakaboo.filter.model.FilterSet;

public class SerializedFilter {

	String settings;
	String clazz;
	
	public SerializedFilter() {	}
	
	public SerializedFilter(Filter filter) {
		
		settings = filter.save();
		System.out.println(settings);
		clazz = filter.getClass().getName();
		
	}
	
	public Filter create() {
		FilterSet filterset = new FilterSet();
		for (Filter f : filterset.getAvailableFilters()) {
			if (f.getClass().getName() == clazz) {
				System.out.println(settings);
				f.load(settings);
				return f;
			}
		}
		return null;
	}



	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getSettings() {
		return settings;
	}

	public void setSettings(String settings) {
		this.settings = settings;
	}

	
	
	
	
	
	
	
}
