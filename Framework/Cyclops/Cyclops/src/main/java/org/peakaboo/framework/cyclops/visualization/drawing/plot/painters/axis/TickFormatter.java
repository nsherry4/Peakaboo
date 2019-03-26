package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis;

import java.util.function.Function;

public class TickFormatter {
	public Float start, end;
	Function<Integer, String> formatter;
	public boolean log = false;
	
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
}