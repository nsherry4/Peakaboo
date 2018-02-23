package peakaboo.controller.plotter.settings;


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
		case NONE:    return "Individual Scan";
		case AVERAGE: return "Mean per Channel";
		case MAXIMUM: return "Max per Channel";
		}
		return "Unknown";
	}

	
	
}
