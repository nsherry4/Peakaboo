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
		case NONE: return "Individual Spectrum";
		case AVERAGE: return "Mean Average of Spectra";
		case MAXIMUM: return "Strongest Signal per Channel";
		}
		return "Unknown Composite Mode";
	}

	
	
}
