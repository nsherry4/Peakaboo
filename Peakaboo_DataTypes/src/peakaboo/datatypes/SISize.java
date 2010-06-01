package peakaboo.datatypes;


public enum SISize
{

	pm, 
	nm, 
	um, 
	mm, 
	m, 
	km;
	
	public static SISize lower(SISize size)
	{
			
		int ordinal = size.ordinal();
		
		ordinal--;
		if (ordinal < 0) return null;
		if (ordinal >= SISize.values().length) return null;
		
		return SISize.values()[ordinal];
		
	}
	
	public static SISize raise(SISize size)
	{
		
		int ordinal = size.ordinal();

		ordinal++;
		if (ordinal < 0) return null;
		if (ordinal >= SISize.values().length) return null;
		
		return SISize.values()[ordinal];
		
	}
	
}
