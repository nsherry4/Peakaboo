package peakaboo.curvefit.transitionseries;

import peakaboo.curvefit.transition.Transition;



/**
 * This lists the various series of {@link Transition}s, such as all of the K transitions
 * 
 * @author Nathaniel Sherry, 2009-2010
 */

public enum TransitionSeriesType
{

	K {

		@Override
		public String toString()
		{
			return this.name() + " Series";
		}
	},
	L {

		@Override
		public String toString()
		{
			return this.name() + " Series";
		}
	},
	M {

		@Override
		public String toString()
		{
			return this.name() + " Series";
		}
	},
	COMPOSITE {

		@Override
		public String toString()
		{
			return "Composite Series";
		}
	};
	
	
	public static TransitionSeriesType fromTypeString(String type)
	{
		return TransitionSeriesType.valueOf(type);
	}

}
