package peakaboo.controller.plotter.settings;

/**
 * 
 * This enum lists the kinds of ways that a collection of scans can be composited together for user viewing
 * 
 * @author Nathaniel Sherry, 2009
 */

public enum ChannelCompositeMode
{

	NONE {
		@Override
		public String prettyprint()
		{
			return "Single Scan";
		}
	},
	AVERAGE {
		@Override
		public String prettyprint()
		{
			return "Average of Scans";
		}
	},
	MAXIMUM {
		@Override
		public String prettyprint()
		{
			return "Maximum per Channel";
		}
	}
	
	;
	
	public abstract String prettyprint();

	
	
}
