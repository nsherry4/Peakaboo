package org.peakaboo.mapping.filter.plugin;

import org.peakaboo.datalabel.DataLabel;

public class MapFilterDescriptor {

	public static final String GROUP_SIZING = "Sizing";
	public static final String GROUP_SMOOTHING = "Smoothing";
	public static final String GROUP_CLIPPING = "Clipping";
	public static final String GROUP_MATH = "Mathematical";
	public static final String GROUP_TRANSFORMING = "Transforming";
	public static final String GROUP_ENHANCING = "Enhancing";
	public static final String GROUP_OTHER = "Other";

	public static final String ACTION_SIZED = DataLabel.SIZED.getText();
	public static final String ACTION_SMOOTHED = DataLabel.SMOOTHED.getText();
	public static final String ACTION_CLIPPED = DataLabel.CLIPPED.getText();
	public static final String ACTION_FILTERED = DataLabel.FILTERED.getText();
	public static final String ACTION_TRANSFORMED = DataLabel.TRANSFORMED.getText();
	public static final String ACTION_SHARPENED = DataLabel.SHARPENED.getText();

	public static final MapFilterDescriptor SIZING = new MapFilterDescriptor(GROUP_SIZING, DataLabel.SIZED);
	public static final MapFilterDescriptor SMOOTHING = new MapFilterDescriptor(GROUP_SMOOTHING, DataLabel.SMOOTHED);
	public static final MapFilterDescriptor CLIPPING = new MapFilterDescriptor(GROUP_CLIPPING, DataLabel.CLIPPED);
	public static final MapFilterDescriptor MATH = new MapFilterDescriptor(GROUP_MATH, DataLabel.FILTERED);
	public static final MapFilterDescriptor TRANSFORMING = new MapFilterDescriptor(GROUP_TRANSFORMING, DataLabel.TRANSFORMED);
	public static final MapFilterDescriptor SHARPENING = new MapFilterDescriptor(GROUP_ENHANCING, DataLabel.SHARPENED);


	private String group;
	private DataLabel label;

	public MapFilterDescriptor(String group, DataLabel label) {
		this.group = group;
		this.label = label;
	}

	public MapFilterDescriptor(String group, String action) {
		this(group, new DataLabel(action));
	}

	public String getGroup() {
		return group;
	}

	public DataLabel getLabel() {
		return label;
	}

	public String getAction() {
		return label.getText();
	}



}
