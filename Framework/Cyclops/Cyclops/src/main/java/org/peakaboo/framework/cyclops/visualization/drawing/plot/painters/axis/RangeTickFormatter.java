package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.PlotDrawing;

public class RangeTickFormatter extends AbstractTickFormatter {
	
	private float tickStart, tickEnd;
	private Function<Integer, String> formatter;
	
	
	public RangeTickFormatter(float tickStart, float tickEnd) {
		this(tickStart, tickEnd, String::valueOf);
	}
	
	public RangeTickFormatter(float tickStart, float tickEnd, Function<Integer, String> formatter) {
		this.tickStart = tickStart;
		this.tickEnd = tickEnd;
		this.formatter = formatter;
	}
	
	
	@Override
	public List<TickMark> getTickMarks(PainterData p, float size) {
		var marks = new ArrayList<TickMark>();
		
		if (this.isEmpty()) return marks;
		
		float tickRange = this.tickEnd - this.tickStart;
		float maxTicks = this.calcMaxTicks(p, size);
		int increment = getIncrement(tickRange, maxTicks, 1);
		if (increment == 0) return marks;
		int startingValue = (int)(this.tickStart + (Math.abs(this.tickStart) % increment));
		
		int currentValue = startingValue;
		while (currentValue <= this.tickEnd) {
			var position = (currentValue - this.tickStart)  / tickRange;
			
			float tickValue;
			if (this.isLog()) {
				tickValue = (float)Math.exp(  (position) * Math.log1p(tickRange)  ) - 1.0f;
				tickValue = SigDigits.toIntSigDigit(tickValue, 2);					
			} else {
				tickValue = currentValue;
			}
			
			var text = this.format((int)tickValue);
			if (position >= 0 || position <= 1) {
				marks.add(new TickMark(text, position));
			}
			currentValue += increment;
		}
		
		return marks;
	}
	
	@Override
	public TickTextSize maxTextSize(PainterData p) {
		
		float height = p.context.getFontHeight();
		
		float startWidth = p.context.getTextWidth(this.format(  SigDigits.toIntSigDigit(this.tickStart, 2)  ));			
		float endWidth = p.context.getTextWidth(this.format(  SigDigits.toIntSigDigit(PlotDrawing.getDataScale(this.tickEnd, false, this.isPadded()), 2)  ));
		float width = Math.max(startWidth, endWidth);
		
		return new TickTextSize(width, height);
		
	}
	
	@Override
	public boolean isEmpty() {
		return this.tickEnd - this.tickStart <= 0;
	}

	private String format(int value) {
		return this.formatter.apply(value);
	}
	







	private float calcMaxTicks(PainterData p, float freeSpace) {
		//how many ticks we can fit and the range of values we're drawing over
		float maxTicks = 0;
		if (this.isTextRotated()) {
			float textHeight = p.context.getFontHeight();
			maxTicks = (float) Math.floor(freeSpace / (textHeight*3.0));
			return maxTicks;
		} else {
			// text isn't rotated out so calculate the maximum width of a text entry here
			int maxValue = (int) (this.tickEnd);
			String text = this.formatter.apply(maxValue);
			float maxWidth = p.context.getTextWidth(text) + 4;
			if (maxWidth < 1) { maxWidth = 1; }
			maxTicks = freeSpace / (maxWidth * 3.0f);
		}
		return maxTicks;
	}
	
	private static int getIncrement(float valueRange, float maxTickCount, int significantDigits) {		
		if (maxTickCount == 0) return Integer.MAX_VALUE;
		return SigDigits.toIntSigDigit(valueRange / maxTickCount, significantDigits);
	}
	
	
}