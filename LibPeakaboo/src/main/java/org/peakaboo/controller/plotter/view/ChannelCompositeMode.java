package org.peakaboo.controller.plotter.view;


/**
 * 
 * This enum lists the kinds of ways that a collection of scans can be composited together for user viewing
 * 
 * @author Nathaniel Sherry, 2009
 */

public enum ChannelCompositeMode
{

	NONE,
	AVERAGE,
	MAXIMUM
	
	;
	
	public String show() {
		switch (this) {
		case NONE:    return "Individual Spectrum";
		case AVERAGE: return "Mean per Channel";
		case MAXIMUM: return "Max per Channel";
		}
		return "Unknown";
	}
	
	public String shortName() {
		switch (this) {
		case NONE:    return "Individual";
		case AVERAGE: return "Mean";
		case MAXIMUM: return "Max";
		}
		return "Unknown";
	}

	
	
}
