package org.peakaboo.mapping.filter.plugin;

public class MapFilterDescriptor {
	
	public static String GROUP_SIZING = "Sizing";
	public static String GROUP_SMOOTHING = "Smoothing";
	public static String GROUP_CLIPPING = "Clipping";
	public static String GROUP_MATH = "Mathematical";
	public static String GROUP_TRANSFORMING = "Transforming";
	public static String GROUP_ENHANCING = "Enhancing";
	public static String GROUP_OTHER = "Other";

	public static String ACTION_SIZED = "Sized";
	public static String ACTION_SMOOTHED = "Smoothed";
	public static String ACTION_CLIPPED = "Clipped";
	public static String ACTION_FILTERED = "Filtered";
	public static String ACTION_TRANSFORMED = "Transformed";
	public static String ACTION_SHARPENED = "Sharpened";
	
	public static MapFilterDescriptor SIZING = new MapFilterDescriptor(GROUP_SIZING, ACTION_SIZED);
	public static MapFilterDescriptor SMOOTHING = new MapFilterDescriptor(GROUP_SMOOTHING, ACTION_SMOOTHED);
	public static MapFilterDescriptor CLIPPING = new MapFilterDescriptor(GROUP_CLIPPING, ACTION_CLIPPED);
	public static MapFilterDescriptor MATH = new MapFilterDescriptor(GROUP_MATH, ACTION_FILTERED);
	public static MapFilterDescriptor TRANSFORMING = new MapFilterDescriptor(GROUP_TRANSFORMING, ACTION_TRANSFORMED);
	public static MapFilterDescriptor SHARPENING = new MapFilterDescriptor(GROUP_ENHANCING, ACTION_SHARPENED);
	
	
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
