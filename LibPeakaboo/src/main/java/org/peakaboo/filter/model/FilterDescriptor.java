package org.peakaboo.filter.model;

import org.peakaboo.datalabel.DataLabel;

public class FilterDescriptor {

	public static final String ACTION_SMOOTHED = DataLabel.SMOOTHED.getText();
	public static final String ACTION_BACKGROUND = DataLabel.BACKGROUND_REMOVED.getText();
	public static final String ACTION_OTHER = DataLabel.OTHER_FILTERING.getText();

	public static final FilterDescriptor SMOOTHING = new FilterDescriptor(FilterType.SMOOTHING, DataLabel.SMOOTHED);
	public static final FilterDescriptor BACKGROUND = new FilterDescriptor(FilterType.BACKGROUND, DataLabel.BACKGROUND_REMOVED);
	public static final FilterDescriptor OTHER = new FilterDescriptor(FilterType.OTHER, DataLabel.OTHER_FILTERING);
	public static final FilterDescriptor ADVANCED = new FilterDescriptor(FilterType.ADVANCED, DataLabel.OTHER_FILTERING);
	public static final FilterDescriptor MATHEMATICAL = new FilterDescriptor(FilterType.MATHEMATICAL, DataLabel.OTHER_FILTERING);
	public static final FilterDescriptor PROGRAMMING = new FilterDescriptor(FilterType.PROGRAMMING, DataLabel.OTHER_FILTERING);


	private FilterType type;
	private DataLabel label;

	public FilterDescriptor(FilterType type, DataLabel label) {
		this.type = type;
		this.label = label;
	}

	public FilterDescriptor(FilterType type, String action) {
		this(type, new DataLabel(action));
	}

	public FilterType getType() {
		return type;
	}

	public DataLabel getLabel() {
		return label;
	}

	public String getAction() {
		return label.getText();
	}

}
