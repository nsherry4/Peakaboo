package peakaboo.datatypes;

import java.math.BigDecimal;


public class SigDigits
{


	public static int toIntSigDigit(double value, int significantDigits)
	{
		
		if (value == 0.0) return (int)value;
		
		if (significantDigits < 1) significantDigits = 1;
		
		int cutoff = (int)Math.pow(10, significantDigits);
		int cutoffSmall = (int)Math.pow(10, significantDigits-1);
		
		int counts = 0;
		if (value > cutoff) {
			while (value > cutoff)
			{
				value /= 10.0;
				counts++;
			}
			value = Math.ceil(value);
			while (counts > 0)
			{
				value *= 10.0;
				counts--;
			}
		} else {
			while (value < cutoffSmall)
			{
				value *= 10.0;
				counts++;
			}
			value = Math.ceil(value);
			while (counts > 0)
			{
				value /= 10.0;
				value = Math.ceil(value);
				
				counts--;
			}
		}
		return (int)value;
	}
	
	public static String roundFloatTo(float value, int decimals)
	{
	
		try {
			BigDecimal bd = new BigDecimal(Float.toString(value));
			bd = bd.setScale(decimals, BigDecimal.ROUND_HALF_EVEN);
			
			String result = bd.toPlainString();
			if (Float.parseFloat(result) == 0 && decimals < 4) 
				return roundFloatTo(value, decimals+1);
			else
				return result;
			
		} catch (Exception e)
		{
			return "?";
		}
		
	}
	
}
