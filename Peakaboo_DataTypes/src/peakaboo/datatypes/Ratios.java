package peakaboo.datatypes;


public class Ratios
{

	public static final int logValue = 2;
	
	public static String fromFloat(float value)
	{
		return fromFloat(value, false);
	}
	
	public static String fromFloat(float value, boolean integersOnly)
	{
		float ratioValue = (float)Math.pow(logValue, Math.abs(value));
		int decimals = 0;;
		if (ratioValue < logValue && !integersOnly) decimals = 1;
		
		String ratioString; 
		ratioString = SigDigits.roundFloatTo(ratioValue, decimals);
		
		String ratio = "";
		if (value < 0) ratio = "1:" + ratioString;
		if (value > 0) ratio = ratioString + ":1";
		if (value == 0) ratio = "1:1";
		
		return ratio;
	}
	
}
