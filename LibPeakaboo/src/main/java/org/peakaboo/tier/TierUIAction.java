package org.peakaboo.tier;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class TierUIAction<V, C> {

	public String text, iconname, tooltip, location;
	public BiConsumer<V, C> action; //view, controller
	public Object component; //UI can stash component here for easy tracking. This is NOT for the Tier to populate
	public Function<C, Boolean> enabled;
	
	public TierUIAction(String location, String text, String iconname, String tooltip, BiConsumer<V, C> action) {
		this(location, text, iconname, tooltip, action, c -> true);
	}
	
	public TierUIAction(String location, String text, String iconname, String tooltip, BiConsumer<V, C> action, Function<C, Boolean> enabled) {
		this.location = location;
		this.text = text;
		this.iconname = iconname;
		this.tooltip = tooltip;
		
		this.action = action;
		this.enabled = enabled;
	}
	
}
