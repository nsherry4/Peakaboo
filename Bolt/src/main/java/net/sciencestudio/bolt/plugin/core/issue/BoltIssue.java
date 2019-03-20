package net.sciencestudio.bolt.plugin.core.issue;

/**
 * Represends a problem with a plugin or plugin container
 **/
public interface BoltIssue {

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
