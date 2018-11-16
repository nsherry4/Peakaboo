package peakaboo.curvefit.peak.transition;

/**
 * This lists the various series of {@link Transition}s, such as all of the K transitions
 * 
 * @author Nathaniel Sherry, 2009-2010
 */

public enum TransitionShell
{

	K {

		@Override
		public String toString()
		{
			return this.name() + " Shell";
		}
		
		@Override
		public int shell() {
			return 1;
		}
		
		
		
	},
	L {

		@Override
		public String toString()
		{
			return this.name() + " Shell";
		}
		
		@Override
		public int shell() {
			return 2;
		}
		
	},
	M {

		@Override
		public String toString()
		{
			return this.name() + " Shell";
		}
		
		@Override
		public int shell() {
			return 3;
		}
		
	},
	COMPOSITE {

		@Override
		public String toString()
		{
			return "Composite";
		}
		
		@Override
		public int shell() {
			return -1;
		}
		
	};
	
	
	public int shell() {
		throw new UnsupportedOperationException();
	}
	
	public static TransitionShell fromTypeString(String type)
	{
		return TransitionShell.valueOf(type);
	}

	
}
