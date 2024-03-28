package org.peakaboo.tier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.peakaboo.framework.autodialog.model.SelfDescribing;

public class TierUIAction<V, C> implements SelfDescribing {

	public String text, iconname, desc, location;
	public BiConsumer<V, C> action; //view, controller
	public Object component; //UI can stash component here for easy tracking. This is NOT for the Tier to populate
	public Function<C, Boolean> enabled;
	
	public TierUIAction(String location, String text, String iconname, String tooltip, BiConsumer<V, C> action) {
		this(location, text, iconname, tooltip, action, c -> true);
	}
	
	public TierUIAction(String location, String text, String iconname, String description, BiConsumer<V, C> action, Function<C, Boolean> enabled) {
		this.location = location;
		this.text = text;
		this.iconname = iconname;
		this.desc = description;
		
		this.action = action;
		this.enabled = enabled;
	}

	@Override
	public String name() {
		return this.text;
	}

	@Override
	public String description() {
		return this.desc;
	}
	
	
}
