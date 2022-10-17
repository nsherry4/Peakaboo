package org.peakaboo.tier;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TierUIItem {

	public String text, iconname, tooltip, location;
	public BiConsumer<Object, Object> action; //view, controller
	public Object component;
	public Function<Object, Boolean> enabled;
	
	public TierUIItem(String location, String text, String iconname, String tooltip, BiConsumer<Object, Object> action) {
		this(location, text, iconname, tooltip, action, c -> true);
	}
	
	public TierUIItem(String location, String text, String iconname, String tooltip, BiConsumer<Object, Object> action, Function<Object, Boolean> enabled) {
		this.location = location;
		this.text = text;
		this.iconname = iconname;
		this.tooltip = tooltip;
		
		this.action = action;
		this.enabled = enabled;
	}
	
}
