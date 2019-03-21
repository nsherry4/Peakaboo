package net.sciencestudio.autodialog.model.style;

import java.util.Optional;

public interface Style<T> {

	/**
	 * Returns a style hint for mapping a style to a concrete UI component
	 */
	String getStyle();
	
	/**
	 * Returns a fallback {@link CoreStyle} in case there is no concrete 
	 * UI component for this style in a specific UI implementation.
	 */
	CoreStyle getFallbackStyle();
	
	/**
	 * Gets the override value for vertical expand
	 */
	Optional<Boolean> getVerticalExpand();
	/**
	 * Sets the override value for vertical expand
	 */
	Style<T> setVerticalExpand(Optional<Boolean> override);
	
	default Style<T> setVerticalExpand(Boolean override) {
		setHorizontalExpand(Optional.ofNullable(override));
		return this;
	}
	
	/**
	 * Gets the override value for horizontal expand
	 */
	Optional<Boolean> getHorizontalExpand();
	
	/**
	 * Sets the override value for horizontal expand
	 */
	Style<T> setHorizontalExpand(Optional<Boolean> override);
	
	default Style<T> setHorizontalExpand(Boolean override) {
		setHorizontalExpand(Optional.ofNullable(override));
		return this;
	}
	
}
