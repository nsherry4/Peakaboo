package peakaboo.datatypes.peaktable;



/**
 * This lists the various series of {@link Transition}s, such as all of the K transitions
 * 
 * @author Nathaniel Sherry, 2009
 */

public enum TransitionSeriesType
{

	K {

		public String toString()
		{
			return this.name() + " Series";
		}
	},
	L {

		public String toString()
		{
			return this.name() + " Series";
		}
	},
	M {

		public String toString()
		{
			return this.name() + " Series";
		}
	},
	COMPOSITE {

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
