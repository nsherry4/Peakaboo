package net.sciencestudio.autodialog.model.style;

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
	
}
