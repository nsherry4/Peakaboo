package peakaboo.curvefit.fitting;


public enum EscapePeakType
{
	
	SILICON {
		@Override
		public float offset()
		{
			return 1.74f;
		}
	}
	,
	GERMANIUM {
		@Override
		public float offset()
		{
			return 3f;
		}
	}		
	,
	NONE {
		@Override
		public boolean hasOffset()
		{
			return false;
		}
	}
	,
	
	;
	
	public boolean hasOffset() 	{ return true; }
	public float offset()		{ return 1.74f; }
	

}
