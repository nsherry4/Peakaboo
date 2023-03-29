package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.peakaboo.framework.cyclops.SigDigits;
import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.PlotDrawing;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.TickFormatter.TickMark;
import org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis.TickFormatter.TickTextSize;

public class RangeTickFormatter implements TickFormatter {
	
	private Float start, end;
	private Function<Integer, String> formatter;
	private boolean log = false;
	private boolean pad = false;
	
	// Rotates text 90 degrees so that it is running perpendicular to the axis instead of parallel
	private boolean textRotate = false;
	
	// Percentage value where 1 = 100%. Used to scale the size of tickmarks, with 1 being 100%
	private float tickScale = 1f;
	
	
	public boolean isEmpty() {
		return this.getEnd() - this.getStart() <= 0;
	}
	
	private String format(Integer value) {
		return this.formatter.apply(value);
	}
	
	public RangeTickFormatter(float start, float end) {
		this(start, end, String::valueOf);
	}
	
	public RangeTickFormatter(float start, float endstop, Function<Integer, String> formatter) {
		this.start = start;
		this.end = endstop;
		this.formatter = formatter;
	}
	
	@Override
	public RangeTickFormatter withLog(boolean log) {
		this.log = log;
		return this;
	}
	
	@Override
	public boolean isLog() {
		return log;
	}
	
	/**
	 * Accepts a float between 0 and 1, where 1 represents full size (100%)
	 */
	@Override
	public RangeTickFormatter withTickSize(float percent) {
		this.tickScale = percent;
		return this;
	}

	@Override
	public float getTickSize() {
		return tickScale;
	}

	
	
	@Override
	public RangeTickFormatter withRotate(boolean rotate) {
		this.textRotate = rotate;
		return this;
	}
	
	@Override
	public boolean isTextRotated() {
		return textRotate;
	}
	
	
	@Override
	public boolean isPadded() {
		return pad;
	}
	
	@Override
	public RangeTickFormatter withPad(boolean pad) {
		this.pad = pad;
		return this;
	}
	


	public Float getStart() {
		return start;
	}

	public Float getEnd() {
		return end;
	}

	

	public List<TickMark> getTickMarks(PainterData p, float size) {
		var marks = new ArrayList<TickMark>();
		
		float valueRange = this.getEnd() - this.getStart();
		float maxTicks = this.calcMaxTicks(p, size);
		int increment = getIncrement(valueRange, maxTicks, 1); //TODO move this here
		int startingValue = (int)(this.getStart() + (Math.abs(this.getStart()) % increment));
		
		int currentValue = startingValue;
		while (currentValue <= this.getEnd()) {
			var position = (currentValue - this.getStart())  / valueRange;
			
			float tickValue;
			if (this.isLog()) {
				tickValue = (float)Math.exp(  (position) * Math.log1p(valueRange)  ) - 1.0f;
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


	
	public TickTextSize maxTextSize(PainterData p) {
		
		float height = p.context.getFontHeight();
		
		float startWidth = p.context.getTextWidth(this.format(  SigDigits.toIntSigDigit(this.getStart(), 2)  ));			
		float endWidth = p.context.getTextWidth(this.format(  SigDigits.toIntSigDigit(PlotDrawing.getDataScale(this.getEnd(), false, this.isPadded()), 2)  ));
		float width = Math.max(startWidth, endWidth);
		
		return new TickTextSize(width, height);
		
	}


	private float calcMaxTicks(PainterData p, float freeSpace) {
		//how many ticks we can fit and the range of values we're drawing over
		float maxTicks = 0;
		if (this.textRotate) {
			float textHeight = p.context.getFontHeight();
			maxTicks = (float) Math.floor(freeSpace / (textHeight*3.0));
			return maxTicks;
		} else {
			// text isn't rotated out so calculate the maximum width of a text entry here
			int maxValue = (int) (this.end.floatValue());
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