package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis;

import java.util.ArrayList;
import java.util.List;

import org.peakaboo.framework.cyclops.Bounds;
import org.peakaboo.framework.cyclops.Pair;
import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.visualization.Surface;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;


public class AxisMarkGenerator
{

	public static List<Pair<Float, Integer>> getAxisMarkList(float maxTicks, float axisHeight, int incrementSigDigits, float valueRangeStart, float valueRangeEnd)
	{
		
	
		float valueRange = valueRangeEnd - valueRangeStart;
		List<Pair<Float, Integer>> ticks = new ArrayList<Pair<Float, Integer>>();

		//bouhnds/sanity check
		if (maxTicks <= 0) return ticks;
		if (axisHeight <= 0) return ticks;
		
		// Calculate the increment size;
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

	
	public static void drawLine(Surface context, float startx, float starty, float endx, float endy)
	{
		context.setAntialias(false);
		context.moveTo(startx,  starty);
		context.lineTo(endx, endy);
		context.stroke();
		context.setAntialias(true);
	}
	
	public static float calcMaxTicks(PainterData p, TickFormatter tick, float freeSpace) {
		//how many ticks we can fit and the range of values we're drawing over
		float maxTicks = 0;
		if (tick.textRotate) {
			float textHeight = p.context.getFontHeight();
			maxTicks = (float) Math.floor(freeSpace / (textHeight*3.0));
			return maxTicks;
		} else {
			// text isn't rotated out so calculate the maximum width of a text entry here
			int maxValue = (int) (tick.end.floatValue());
			String text = tick.formatter.apply(maxValue);
			float maxWidth = p.context.getTextWidth(text);
			maxTicks = freeSpace / (maxWidth * 3.0f);
		}
		return maxTicks;
	}
	
	public static float calcMaxTicks(PainterData p, TickFormatter tick, Bounds<Float> axisBounds, Pair<Float, Float> otherAxisSize) {
		float freeSpace = axisBounds.end - axisBounds.start - otherAxisSize.first - otherAxisSize.second;
		return calcMaxTicks(p, tick, freeSpace);
	}
	
}
