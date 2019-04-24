package org.peakaboo.framework.bolt.plugin.core.issue;

import java.util.HashMap;
import java.util.Map;

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
	
}
