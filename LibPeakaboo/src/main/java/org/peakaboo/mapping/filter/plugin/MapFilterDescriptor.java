package org.peakaboo.mapping.filter.plugin;

public class MapFilterDescriptor {
	
	public static final String GROUP_SIZING = "Sizing";
	public static final String GROUP_SMOOTHING = "Smoothing";
	public static final String GROUP_CLIPPING = "Clipping";
	public static final String GROUP_MATH = "Mathematical";
	public static final String GROUP_TRANSFORMING = "Transforming";
	public static final String GROUP_ENHANCING = "Enhancing";
	public static final String GROUP_OTHER = "Other";

	public static final String ACTION_SIZED = "Sized";
	public static final String ACTION_SMOOTHED = "Smoothed";
	public static final String ACTION_CLIPPED = "Clipped";
	public static final String ACTION_FILTERED = "Filtered";
	public static final String ACTION_TRANSFORMED = "Transformed";
	public static final String ACTION_SHARPENED = "Sharpened";
	
	public static final MapFilterDescriptor SIZING = new MapFilterDescriptor(GROUP_SIZING, ACTION_SIZED);
	public static final MapFilterDescriptor SMOOTHING = new MapFilterDescriptor(GROUP_SMOOTHING, ACTION_SMOOTHED);
	public static final MapFilterDescriptor CLIPPING = new MapFilterDescriptor(GROUP_CLIPPING, ACTION_CLIPPED);
	public static final MapFilterDescriptor MATH = new MapFilterDescriptor(GROUP_MATH, ACTION_FILTERED);
	public static final MapFilterDescriptor TRANSFORMING = new MapFilterDescriptor(GROUP_TRANSFORMING, ACTION_TRANSFORMED);
	public static final MapFilterDescriptor SHARPENING = new MapFilterDescriptor(GROUP_ENHANCING, ACTION_SHARPENED);
	
	
	private String group, action;
	
	public MapFilterDescriptor(String group, String action) {
		this.group = group;
		this.action = action;
	}

	public String getGroup() {
		return group;
	}

	public String getAction() {
		return action;
	}
	
	
	
}
