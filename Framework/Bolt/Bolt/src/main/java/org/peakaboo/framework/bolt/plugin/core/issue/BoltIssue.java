package org.peakaboo.framework.bolt.plugin.core.issue;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.peakaboo.framework.bolt.plugin.core.BoltPlugin;

/**
 * Represends a problem with a plugin or plugin container
 **/
public interface BoltIssue<T extends BoltPlugin> {

	String title();
	String description();
	String shortSource();
	String longSource();

	default boolean hasFix() {
		return false;
	}

	default boolean fix() {
		throw new IllegalArgumentException("This Issue does not have an available fix");
	}

	default String fixName() {
		return "None";
	}
	
	default boolean isFixDestructuve() {
		return false;
	}
	
	default Map<String, String> infodump() {
		return new HashMap<String, String>() {{
			put("title", title());
			put("description", description());
			put("shortSource", shortSource());
			put("longSource", longSource());
			put("hasFix", Boolean.toString(hasFix()));
			put("isFixDestructuve", Boolean.toString(isFixDestructuve()));
		}};
	}
	
}
