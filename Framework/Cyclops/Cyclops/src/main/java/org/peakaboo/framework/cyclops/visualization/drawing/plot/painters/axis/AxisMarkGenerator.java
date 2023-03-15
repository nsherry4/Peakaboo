package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.cyclops.FloatException;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.visualization.Surface;


public class AxisMarkGenerator
{

	public static List<Pair<Float, Integer>> getAxisMarkList(
			float maxTicks, 
			float axisHeight, 
			int incrementSigDigits, 
			float valueRangeStart, 
			float valueRangeEnd
	) {
		
		
		FloatException.guard(valueRangeStart);
		FloatException.guard(valueRangeEnd);
			
		float valueRange = valueRangeEnd - valueRangeStart;
		List<Pair<Float, Integer>> ticks = new ArrayList<>();

		//bouhnds/sanity check
		if (maxTicks <= 0) return ticks;
		if (axisHeight <= 0) return ticks;
		
		// Calculate the increment size
		int increment = getIncrement(valueRange, maxTicks, incrementSigDigits);

		//calculate the starting value
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
	

	public static int getIncrement(float valueRange, float maxTickCount, int significantDigits) {		
		if (maxTickCount == 0) return Integer.MAX_VALUE;
		return SigDigits.toIntSigDigit(valueRange / maxTickCount, significantDigits);
	}

	
	public static void drawLine(Surface context, float startx, float starty, float endx, float endy) {
		context.setAntialias(false);
		context.moveTo(startx,  starty);
		context.lineTo(endx, endy);
		context.stroke();
		context.setAntialias(true);
	}
	

	

	
}
