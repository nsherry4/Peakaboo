package peakaboo.drawing.plot.painters.axis;

import java.util.List;

import peakaboo.datatypes.DataTypeFactory;
import peakaboo.datatypes.Pair;
import peakaboo.datatypes.SigDigits;
import peakaboo.drawing.backends.Surface;


public class AxisMarkGenerator
{

	public static List<Pair<Double, Integer>> getAxisMarkList(double maxTicks, double axisHeight, int incrementSigDigits, double valueRangeStart, double valueRangeEnd)
	{
		
	
		double valueRange = valueRangeEnd - valueRangeStart;
		List<Pair<Double, Integer>> ticks = DataTypeFactory.<Pair<Double, Integer>>list();
		
		// Calculate the increment size;
		if (maxTicks == 0) return ticks;
		int increment = getIncrement(valueRange, maxTicks, incrementSigDigits);
				
		//calculate the starting value;
		int startingValue = (int)(valueRangeStart + (Math.abs(valueRangeStart) % increment)); 
		int currentValue = startingValue;
	
		double percentAlongAxis;
		while (currentValue < valueRangeEnd){
			
			percentAlongAxis = 1-0 - (currentValue - valueRangeStart)  / valueRange;
			ticks.add(new Pair<Double, Integer>(percentAlongAxis, currentValue));
			currentValue += increment;
			
		}
			
		return ticks;
	}
	

	public static int getIncrement(double valueRange, double maxTickCount, int significantDigits)
	{
		
		if (maxTickCount == 0) return Integer.MAX_VALUE;
		return SigDigits.toIntSigDigit(valueRange / maxTickCount, significantDigits);
		
	}
	
	public static int getMaxTicksY(Surface context, double axisSize)
	{		
		double textHeight = context.getFontHeight();
		int maxTicks = (int)Math.floor(axisSize / (textHeight*3.0));
		
		return maxTicks;
	}
	
	public static void drawTick(Surface context, double startx, double starty, double endx, double endy)
	{
		context.setAntialias(false);
		context.moveTo(startx,  starty);
		context.lineTo(endx, endy);
		context.stroke();
		context.setAntialias(true);
	}
	
}
