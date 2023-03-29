package org.peakaboo.framework.cyclops.visualization.drawing.plot.painters.axis;

import java.util.List;

import org.peakaboo.framework.cyclops.visualization.drawing.painters.PainterData;

public interface TickFormatter {

	boolean isEmpty();
	
	TickFormatter withLog(boolean log);

	boolean isLog();

	/**
	 * Accepts a float between 0 and 1, where 1 represents full size (100%)
	 */
	TickFormatter withTickSize(float percent);

	float getTickSize();

	TickFormatter withRotate(boolean rotate);

	boolean isTextRotated();

	boolean isPadded();

	TickFormatter withPad(boolean pad);

	
	
	
	record TickTextSize(float width, float height) {};
	TickTextSize maxTextSize(PainterData p);
		
	record TickMark(String value, float position) {};
	List<TickMark> getTickMarks(PainterData p, float size);
	
}



