package org.peakaboo.framework.cyclops;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class SigDigits
{


	public static int toIntSigDigit(double value, int significantDigits)
	{
		if (!Double.isFinite(value)) value = 0;
		if (value == 0.0) return (int)value;
		
		if (significantDigits < 1) significantDigits = 1;
		
		int upper = 	(int)Math.pow(10, significantDigits);
		int lower = 	(int)Math.pow(10, significantDigits-1);
		
		boolean negative = value < 0d;
		if (negative) value = Math.abs(value);
		
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
		
		if (negative) value *= -1d;
			
		return (int)value;
	}
	
	public static String roundFloatTo(float value, int decimals)
	{
		
		if (! Float.isFinite(value)) return "-";
		BigDecimal bd = new BigDecimal(Float.toString(value));
		bd = bd.setScale(decimals, RoundingMode.HALF_EVEN);
		
		String text = bd.toPlainString();
		while (text.endsWith("0") && text.contains(".") || text.endsWith(".")) { text = text.substring(0, text.length() - 1); }
		return text;
	}

	/** 
	 * Transforms large numbers into human readable values, eg 100,001 -> 100k 
	 */
	public static String humanFormattedNumber(float value) {
		String[] suffixes = new String[] {"", "k", "M", "B", "T"};
		for (String suffix : suffixes) {
			if (value < 1000) {
				return roundFloatTo(value, 1) + suffix;
			}
			value /= 1000f;
		}
		return roundFloatTo(value, 1) + "Q";
	}
	
	
}
