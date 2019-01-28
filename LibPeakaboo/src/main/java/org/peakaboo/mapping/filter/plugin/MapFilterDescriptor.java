package org.peakaboo.mapping.filter.plugin;

public class MapFilterDescriptor {

	public static MapFilterDescriptor SCALING = new MapFilterDescriptor("Scaling", "Scaled");
	public static MapFilterDescriptor SMOOTHING = new MapFilterDescriptor("Smoothing", "Smoothed");
	public static MapFilterDescriptor CLIPPING = new MapFilterDescriptor("Clipping", "Clipped");
	
	private String present, past;
	
	public MapFilterDescriptor(String present, String past) {
		this.present = present;
		this.past = past;
	}

	public String getPresent() {
		return present;
	}

	public String getPast() {
		return past;
	}
	
	
	
}
