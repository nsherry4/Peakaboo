package peakaboo.drawing.plot.painters.axis;

import java.util.List;

import fava.*;
import fava.datatypes.Pair;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.SigDigits;
import peakaboo.drawing.backends.Surface;


public class AxisMarkGenerator
{

	public static List<Pair<Float, Integer>> getAxisMarkList(float maxTicks, float axisHeight, int incrementSigDigits, float valueRangeStart, float valueRangeEnd)
	{
		
	
		float valueRange = valueRangeEnd - valueRangeStart;
		List<Pair<Float, Integer>> ticks = DataTypeFactory.<Pair<Float, Integer>>list();
		
		// Calculate the increment size;
		if (maxTicks == 0) return ticks;
		int increment = getIncrement(valueRange, maxTicks, incrementSigDigits);
				
		//calculate the starting value;
		int startingValue = (int)(valueRangeStart + (Math.abs(valueRangeStart) % increment)); 
		int currentValue = startingValue;
	
		float percentAlongAxis;
		while (currentValue < valueRangeEnd){
			
			percentAlongAxis = 1-0 - (currentValue - valueRangeStart)  / valueRange;
			ticks.add(new Pair<Float, Integer>(percentAlongAxis, currentValue));
			currentValue += increment;
			
		}
			
		return ticks;
	}
	

	public static int getIncrement(float valueRange, float maxTickCount, int significantDigits)
	{
		
		if (maxTickCount == 0) return Integer.MAX_VALUE;
		return SigDigits.toIntSigDigit(valueRange / maxTickCount, significantDigits);
		
	}
	
	public static int getMaxTicksY(Surface context, float axisSize)
	{		
		float textHeight = context.getFontHeight();
		int maxTicks = (int)Math.floor(axisSize / (textHeight*3.0));
		
		return maxTicks;
	}
	
	public static void drawTick(Surface context, float startx, float starty, float endx, float endy)
	{
		context.setAntialias(false);
		context.moveTo(startx,  starty);
		context.lineTo(endx, endy);
		context.stroke();
		context.setAntialias(true);
	}
	
}
