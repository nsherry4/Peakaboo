package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis;

import java.util.function.Function;

import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;

public class TickFormatter {
	public Float start, end;
	Function<Integer, String> formatter;
	public boolean log = false;
	public boolean pad = false;
	
	/**
	 * Rotates text 90 degrees so that it is running perpendicular to the axis instead of parallel
	 */
	public boolean textRotate = false;
	
	/**
	 * Percentage value where 1 = 100%. Used to scale the size of tickmarks, with 1 being 100%
	 */
	public float tickScale = 1f;
	
	
	public TickFormatter(float start, float end) {
		this(start, end, String::valueOf);
	}
	
	public TickFormatter(float start, float endstop, Function<Integer, String> formatter) {
		this.start = start;
		this.end = endstop;
		this.formatter = formatter;
	}
	
	public TickFormatter withLog(boolean log) {
		this.log = log;
		return this;
	}
	
	public TickFormatter withTick(float percent) {
		this.tickScale = percent;
		return this;
	}
	
	public TickFormatter withRotate(boolean rotate) {
		this.textRotate = rotate;
		return this;
	}
	
	public TickFormatter withPad(boolean pad) {
		this.pad = pad;
		return this;
	}
	
	public float calcMaxTicks(PainterData p, float freeSpace) {
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
			float maxWidth = p.context.getTextWidth(text);
			if (maxWidth < 1) { maxWidth = 1; }
			maxTicks = freeSpace / (maxWidth * 3.0f);
		}
		return maxTicks;
	}
	
}