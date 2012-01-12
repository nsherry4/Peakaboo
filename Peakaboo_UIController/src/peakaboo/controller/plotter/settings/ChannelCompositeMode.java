package peakaboo.controller.plotter.settings;

import peakaboo.common.Showable;

/**
 * 
 * This enum lists the kinds of ways that a collection of scans can be composited together for user viewing
 * 
 * @author Nathaniel Sherry, 2009
 */

public enum ChannelCompositeMode implements Showable
{

	NONE {
		@Override
		public String show()
		{
			return "Individual Spectrum";
		}
	},
	AVERAGE {
		@Override
		public String show()
		{
			return "Mean Average of Spectra";
		}
	},
	MAXIMUM {
		@Override
		public String show()
		{
			return "Strongest Signal per Channel";
		}
	}
	
	;
	
	public abstract String show();

	
	
}
