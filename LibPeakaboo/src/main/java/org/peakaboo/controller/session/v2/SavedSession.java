package org.peakaboo.controller.session.v2;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.peakaboo.controller.plotter.view.SessionViewModel;
import org.peakaboo.framework.bolt.plugin.core.SavedPlugin;
import org.peakaboo.framework.druthers.DruthersStorable;


public class SavedSession implements DruthersStorable {
	
	public static final String FORMAT = "org.peakaboo.session/v2";
	
	public String format;
	public SavedData data;
	public List<SavedPlugin> filters;
	public SavedFittings fittings;
	public SessionViewModel view;
	public SavedAppData app;
	public Map<String, Object> extended;
	
	public SavedSession() {}
	
	public SavedSession(
			SavedData data, 
			List<SavedPlugin> filters, 
			SavedFittings fittings, 
			SessionViewModel view,
			SavedAppData app, 
			LinkedHashMap<String, Object> extended
		) {
		this.format = FORMAT;
		this.data = data;
		this.filters = filters;
		this.fittings = fittings;
		this.view = view;
		this.app = app;
		this.extended = new LinkedHashMap<>(extended);
		
	}

}
