package peakaboo.controller.plotter;

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
		public String toString()
		{
			return "Single Scan";
		}

	},
	AVERAGE {

		@Override
		public String toString()
		{
			return "Average of Scans";
		}
	},
	MAXIMUM {

		@Override
		public String toString()
		{
			return "Strongest Signal per Channel";
		}
	};
	
}
