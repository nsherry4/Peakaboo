package peakaboo.datatypes;


public class Ratios
{

	public static String fromDouble(double value)
	{
		return fromDouble(value, false);
	}
	
	public static String fromDouble(double value, boolean integersOnly)
	{
		double ratioValue = Math.pow(10, Math.abs(value));
		int decimals = 0;;
		if (ratioValue < 10 && !integersOnly) decimals = 1;
		
		String ratioString; 
		ratioString = SigDigits.roundDoubleTo(ratioValue, decimals);
		
		String ratio = "";
		if (value < 0) ratio = "1:" + ratioString;
		if (value > 0) ratio = ratioString + ":1";
		if (value == 0) ratio = "1:1";
		
		return ratio;
	}
	
}
