package org.peakaboo.framework.cyclops;

import java.math.BigDecimal;


public class SigDigits
{


	public static int toIntSigDigit(double value, int significantDigits)
	{
		
		if (value == 0.0) return (int)value;
		
		if (significantDigits < 1) significantDigits = 1;
		
		int upper = 	(int)Math.pow(10, significantDigits);
		int lower = 	(int)Math.pow(10, significantDigits-1);
		
		int counts = 0;
		if (value > upper) {
			while (value > upper) { value /= 10.0; counts++; }
			value = Math.ceil(value);
			while (counts > 0) { value *= 10.0; counts--; }
		} else {
			while (value < lower) { value *= 10.0; counts++; }
			value = Math.ceil(value);
			while (counts > 0) { value /= 10.0; value = Math.ceil(value); counts--; }
		}
		return (int)value;
	}
	
	public static String roundFloatTo(float value, int decimals)
	{
		return roundFloatTo(value, decimals, false);
	}
	
	public static String roundFloatTo(float value, int decimals, boolean trimZeroes)
	{
		
		if (Float.isNaN(value)) return "-";
		if (Float.isInfinite(value)) return "-";
		BigDecimal bd = new BigDecimal(Float.toString(value));
		bd = bd.setScale(decimals, BigDecimal.ROUND_HALF_EVEN);
		
		String text = bd.toPlainString();
		while (text.endsWith("0") && text.contains(".") || text.endsWith(".")) { text = text.substring(0, text.length() - 1); }
		return text;
	}

	
}
