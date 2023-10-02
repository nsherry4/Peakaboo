package org.peakaboo.filter.model;

public class FilterDescriptor {

	public static final String ACTION_SMOOTHED = "Smoothed";
	public static final String ACTION_BACKGROUND = "Background Removed";
	public static final String ACTION_OTHER = "Other Filtering";
	
	public static final FilterDescriptor SMOOTHING = new FilterDescriptor(FilterType.SMOOTHING, ACTION_SMOOTHED);
	public static final FilterDescriptor BACKGROUND = new FilterDescriptor(FilterType.BACKGROUND, ACTION_BACKGROUND);
	public static final FilterDescriptor OTHER = new FilterDescriptor(FilterType.OTHER, ACTION_OTHER);
	public static final FilterDescriptor ADVANCED = new FilterDescriptor(FilterType.ADVANCED, ACTION_OTHER);
	public static final FilterDescriptor MATHEMATICAL = new FilterDescriptor(FilterType.MATHEMATICAL, ACTION_OTHER);
	public static final FilterDescriptor PROGRAMMING = new FilterDescriptor(FilterType.PROGRAMMING, ACTION_OTHER);
	
	
	private FilterType type;
	private String action;
	
	public FilterDescriptor(FilterType type, String action) {
		this.type = type;
		this.action = action;
	}

	public FilterType getType() {
		return type;
	}

	public String getAction() {
		return action;
	}
	
}
